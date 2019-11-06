package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var realTimeSeekBarProgress: Int = 2
    private var simTimeSeekBarProgress: Int = 4
    private val realTime: MutableLiveData<Int> = MutableLiveData(getRealTimeFromSeekBarProgress(realTimeSeekBarProgress))
    private val simTime: MutableLiveData<Int> = MutableLiveData(getSimTimeFromSeekBarProgress(simTimeSeekBarProgress))
    var homeTeam: String = ""
    var awayTeam: String = ""

    fun setRealTimeSeekBarProgress(value: Int) {
        realTimeSeekBarProgress = value
        realTime.value = getRealTimeFromSeekBarProgress(value)
    }

    fun setSimTimeSeekBarProgress(value: Int) {
        simTimeSeekBarProgress = value
        simTime.value = getSimTimeFromSeekBarProgress(value)
    }

    fun getRealTime(): LiveData<Int> = realTime

    fun getSimTime(): LiveData<Int> = simTime

    private fun getRealTimeFromSeekBarProgress(progress: Int): Int {
        return when (progress) {
            0 -> 3
            1 -> 5
            2 -> 10
            3 -> 15
            else -> 20
        }
    }

    private fun getSimTimeFromSeekBarProgress(progress: Int): Int {
        return when (progress) {
            0 -> 5
            1 -> 10
            2 -> 20
            3 -> 30
            else -> 45
        }
    }
}