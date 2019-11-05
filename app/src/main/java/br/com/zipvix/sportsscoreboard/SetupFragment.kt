package br.com.zipvix.sportsscoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel
import com.google.android.material.textfield.TextInputEditText

class SetupFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    private lateinit var realTimeSeekBar: SeekBar
    private lateinit var simTimeSeekBar: SeekBar
    private lateinit var realTimeTextView: TextView
    private lateinit var simTimeTextView: TextView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)[MainViewModel::class.java]
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
        simTimeTextView = view?.findViewById(R.id.sim_time_label)!!

        viewModel.getRealTime().observe(this, Observer<Int> { value ->
            realTimeTextView.text = getString(R.string.real_time_label, value)
        })

        viewModel.getSimTime().observe(this, Observer<Int> { value ->
            simTimeTextView.text = getString(R.string.sim_time_label, value)
        })
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.sim_time -> viewModel.setSimTimeSeekBarProgress(progress)
            else -> viewModel.setRealTimeSeekBarProgress(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeekBar?) { }

}