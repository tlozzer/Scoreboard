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

class SetupFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    private lateinit var seekBar: SeekBar
    private lateinit var realTimeTextView: TextView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
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

        seekBar = view?.findViewById(R.id.real_time)!!
        seekBar.setOnSeekBarChangeListener(this)

        realTimeTextView = view?.findViewById(R.id.time_label)!!

        viewModel.getRealTime().observe(this, Observer<Int> { value ->
            realTimeTextView.text = getString(R.string.real_time_label, value)
        })
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        viewModel.setSeekBarProgress(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeekBar?) { }

}