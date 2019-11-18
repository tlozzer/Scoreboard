package br.com.zipvix.sportsscoreboard

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel
import com.bumptech.glide.Glide

class TimerFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var homeTeam: TextView
    private lateinit var awayTeam: TextView
    private lateinit var time: TextView
    private lateinit var homeScore: TextView
    private lateinit var awayScore: TextView
    private lateinit var homeImage: ImageView
    private lateinit var awayImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception(getString(R.string.null_activity_exception))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeTeam = view?.findViewById(R.id.home_name)!!
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
        }
        awayScore.setOnClickListener {
            viewModel.setAwayScore(
                viewModel.getAwayScore().value?.plus(1) ?: 0
            )
        }

        viewModel.getHomeName().observe(this, Observer { name -> homeTeam.text = name })

        viewModel.getHomeTeam().observe(this, Observer { value ->
            Glide.with(this)
                .load(value?.image)
                .placeholder(R.drawable.placeholder_flag)
                .into(homeImage)
        })

        viewModel.getAwayName().observe(this, Observer { name -> awayTeam.text = name })

        viewModel.getAwayTeam().observe(this, Observer { value ->
            Glide.with(this)
                .load(value?.image)
                .placeholder(R.drawable.placeholder_flag)
                .into(awayImage)
        })

        viewModel.getHomeScore().observe(this, Observer { value ->
            homeScore.text = value.toString()
        })

        viewModel.getAwayScore().observe(this, Observer { value ->
            awayScore.text = value.toString()
        })

        viewModel.getSimTime().observe(this, Observer { value ->
            time.text = value.toString()
        })

        viewModel.getTimeInMillisToFinish().observe(this, Observer { value ->
            time.text = getString(R.string.time_format, value)
        })

        viewModel.getStatus().observe(this, Observer { status ->
            if (status == MainViewModel.Status.FINISHED) {
                val mediaPlayer: MediaPlayer? = MediaPlayer.create(activity, R.raw.whistle)
                mediaPlayer?.start()
            }
        })
    }
}
