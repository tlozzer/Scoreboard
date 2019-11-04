package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val realTime: MutableLiveData<Int> = MutableLiveData(getRealTimeFromSeekBarProgress(2))
    var seekBarProgress: Int = 2
        private set

    fun setSeekBarProgress(value: Int) {
        seekBarProgress = value
        realTime.value = getRealTimeFromSeekBarProgress(value)
    }

    fun getRealTime(): LiveData<Int> = realTime

    private fun getRealTimeFromSeekBarProgress(progress: Int): Int {
        return when (progress) {
            0 -> 3
            1 -> 5
            2 -> 10
            3 -> 15
            4 -> 20
            else -> 0
        }
    }
}