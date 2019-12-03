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
import br.com.zipvix.sportsscoreboard.business.Scoreboard
import br.com.zipvix.sportsscoreboard.glide.GlideApp
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage

class TimerFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var homeTeam: TextView
    private lateinit var awayTeam: TextView
    private lateinit var timeLabel: TextView
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

        homeTeam = view?.findViewById(R.id.home_name)!!
        awayTeam = view?.findViewById(R.id.away_name)!!
        timeLabel = view?.findViewById(R.id.time_label)!!
        time = view?.findViewById(R.id.time)!!
        homeScore = view?.findViewById(R.id.home_score)!!
        awayScore = view?.findViewById(R.id.away_score)!!
        homeImage = view?.findViewById(R.id.home_image)!!
        awayImage = view?.findViewById(R.id.away_image)!!

        homeScore.setOnClickListener {
            viewModel.addHomeScore()
            if (loadGoalReady)
                goalMediaPlayer.start()
        }

        awayScore.setOnClickListener {
            viewModel.addAwayScore()
            if (loadGoalReady)
                goalMediaPlayer.start()
        }

        viewModel.currentHalf.observe(this, Observer { half ->
            timeLabel.text = getString(R.string.time_label, half)
        })

        viewModel.homeTeam.observe(this, Observer { team ->
            team?.also {
                homeTeam.text = team.name

                it.image?.also { img ->
                    val gsRef = storage.getReferenceFromUrl(img)
                    GlideApp.with(this)
                        .load(gsRef)
                        .placeholder(R.drawable.placeholder_flag)
                        .into(homeImage)
                }
            }
        })

        viewModel.awayTeam.observe(this, Observer
        { team ->
            team?.also {
                awayTeam.text = team.name

                it.image?.also { img ->
                    val gsRef = storage.getReferenceFromUrl(img)
                    GlideApp.with(this)
                        .load(gsRef)
                        .placeholder(R.drawable.placeholder_flag)
                        .into(awayImage)
                }
            }
        })

        viewModel.homeScore.observe(this, Observer
        { value ->
            homeScore.text = value.toString()
        })

        viewModel.awayScore.observe(this, Observer
        { value ->
            awayScore.text = value.toString()
        })

        viewModel.simTime.observe(this, Observer
        { value ->
            time.text = value.toString()
        })

        viewModel.timeToFinish.observe(this, Observer
        { value ->
            time.text = getString(R.string.time_format, value)
        })

        viewModel.status.observe(this, Observer
        { status ->
            if (status == Scoreboard.Status.FINISHING && loadWhistleReady) {
                whistleMediaPlayer.start()
            }
        })
    }

    companion object {
        private const val WHISTLE_URI =
            "android.resource://br.com.zipvix.sportsscoreboard/raw/whistle"
        private const val GOAL_URI = "android.resource://br.com.zipvix.sportsscoreboard/raw/goal"
    }
}
