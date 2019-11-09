package br.com.zipvix.sportsscoreboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel
import com.google.android.material.textfield.TextInputEditText

class SetupFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    private lateinit var realTimeSeekBar: SeekBar
    private lateinit var simTimeSeekBar: SeekBar
    private lateinit var realTimeTextView: TextView
    private lateinit var simTimeTextView: TextView
    private lateinit var homeTeamEdit: TextInputEditText
    private lateinit var awayTeamEdit: TextInputEditText
    private lateinit var button: Button
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception(getString(R.string.null_activity_exception))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        realTimeSeekBar = view?.findViewById(R.id.real_time)!!
        realTimeSeekBar.setOnSeekBarChangeListener(this)

        simTimeSeekBar = view?.findViewById(R.id.sim_time)!!
        simTimeSeekBar.setOnSeekBarChangeListener(this)

        realTimeTextView = view?.findViewById(R.id.time_label)!!
        viewModel.getRealTime().observe(this, Observer<Long> { value ->
            realTimeTextView.text = getString(R.string.real_time_label, value)
        })

        simTimeTextView = view?.findViewById(R.id.sim_time_label)!!
        viewModel.getSimTime().observe(this, Observer<Long> { value ->
            simTimeTextView.text = getString(R.string.sim_time_label, value)
        })

        homeTeamEdit = view?.findViewById(R.id.home_team_edit)!!
        homeTeamEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setHomeTeam(s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        awayTeamEdit = view?.findViewById(R.id.away_team_edit)!!
        awayTeamEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setAwayTeam(s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        button = view?.findViewById(R.id.start)!!
        button.setOnClickListener {
            viewModel.start()
            (activity as MainActivity).viewPager.setCurrentItem(1, true)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.sim_time -> viewModel.setSimTimeSeekBarProgress(progress)
            else -> viewModel.setRealTimeSeekBarProgress(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}