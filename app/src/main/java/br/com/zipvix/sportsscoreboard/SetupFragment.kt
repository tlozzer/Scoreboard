package br.com.zipvix.sportsscoreboard

import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel

class SetupFragment : Fragment(R.layout.fragment_main), SeekBar.OnSeekBarChangeListener {

    private var seekBar: SeekBar? = null
    val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        seekBar = view?.findViewById(R.id.real_time)
        seekBar?.setOnSeekBarChangeListener(this)

        viewModel.getRealTime().observe(activity, Observer {  })
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        viewModel.setSeekBarProgress(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeekBar?) { }

}