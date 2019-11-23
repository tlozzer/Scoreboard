package br.com.zipvix.sportsscoreboard

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.zipvix.sportsscoreboard.glide.GlideApp
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel
import com.google.firebase.storage.FirebaseStorage

class TimerFragment : Fragment() {

    private val WHISTLE_URI = "android.resource://br.com.zipvix.sportsscoreboard/raw/whistle"
    private val GOAL_URI = "android.resource://br.com.zipvix.sportsscoreboard/raw/goal"

    private lateinit var viewModel: MainViewModel
    private lateinit var homeTeam: TextView
    private lateinit var awayTeam: TextView
    private lateinit var time: TextView
    private lateinit var homeScore: TextView
    private lateinit var awayScore: TextView
    private lateinit var homeImage: ImageView
    private lateinit var awayImage: ImageView
    private val whistleMediaPlayer = MediaPlayer()
    private val goalMediaPlayer = MediaPlayer()
    private var loadWhistleReady = false
    private var loadGoalReady = false
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception(getString(R.string.null_activity_exception))

        whistleMediaPlayer.apply {
            setDataSource(requireActivity(), Uri.parse(WHISTLE_URI))
            prepareAsync()
            setOnPreparedListener { loadWhistleReady = true }
        }

        goalMediaPlayer.apply {
            setDataSource(requireActivity(), Uri.parse(GOAL_URI))
            prepareAsync()
            setOnPreparedListener { loadGoalReady = true }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeTeam = view?.findViewById<TextView>(R.id.home_name)?.also {
            viewModel.getHomeName().observe(this, Observer { name -> it.text = name })
        } ?: throw Exception("Invalid home team view")

        awayTeam = view?.findViewById(R.id.away_name)!!
        time = view?.findViewById(R.id.time)!!
        homeScore = view?.findViewById(R.id.home_score)!!
        awayScore = view?.findViewById(R.id.away_score)!!
        homeImage = view?.findViewById(R.id.home_image)!!
        awayImage = view?.findViewById(R.id.away_image)!!

        homeScore.setOnClickListener {
            viewModel.setHomeScore(
                viewModel.getHomeScore().value?.plus(1) ?: 0
            )
            goalMediaPlayer.start()
        }

        awayScore.setOnClickListener {
            viewModel.setAwayScore(
                viewModel.getAwayScore().value?.plus(1) ?: 0
            )
            goalMediaPlayer.start()
        }

        viewModel.getHomeTeam().observe(this, Observer { team ->
            team?.image?.also {
                val gsRef = storage.getReferenceFromUrl(it)
                GlideApp.with(this)
                    .load(gsRef)
                    .placeholder(R.drawable.placeholder_flag)
                    .into(homeImage)
            }
        })

        viewModel.getAwayName().observe(this, Observer
        { name -> awayTeam.text = name })

        viewModel.getAwayTeam().observe(this, Observer
        { team ->
            team?.image?.also {
                val gsRef = storage.getReferenceFromUrl(it)
                GlideApp.with(this)
                    .load(gsRef)
                    .placeholder(R.drawable.placeholder_flag)
                    .into(awayImage)
            }
        })

        viewModel.getHomeScore().observe(this, Observer
        { value ->
            homeScore.text = value.toString()
        })

        viewModel.getAwayScore().observe(this, Observer
        { value ->
            awayScore.text = value.toString()
        })

        viewModel.getSimTime().observe(this, Observer
        { value ->
            time.text = value.toString()
        })

        viewModel.getTimeInMillisToFinish().observe(this, Observer
        { value ->
            time.text = getString(R.string.time_format, value)
        })

        viewModel.getStatus().observe(this, Observer
        { status ->
            if (status == MainViewModel.Status.FINISHING && loadWhistleReady) {
                whistleMediaPlayer.start()
            }
        })
    }
}
