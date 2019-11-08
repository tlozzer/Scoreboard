package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var realTimeSeekBarProgress: Int = 2
    private var simTimeSeekBarProgress: Int = 4
    private val realTime: MutableLiveData<Int> =
        MutableLiveData(getRealTimeFromSeekBarProgress(realTimeSeekBarProgress))
    private val simTime: MutableLiveData<Int> =
        MutableLiveData(getSimTimeFromSeekBarProgress(simTimeSeekBarProgress))
    private val homeTeam: MutableLiveData<String> = MutableLiveData("")
    private val awayTeam: MutableLiveData<String> = MutableLiveData("")
    private val homeScore: MutableLiveData<Int> = MutableLiveData(0)
    private val awayScore: MutableLiveData<Int> = MutableLiveData(0)
    private val timeUntilFinish: MutableLiveData<Long> = MutableLiveData(simTime.value?.toLong() ?: 0)

    fun setRealTimeSeekBarProgress(value: Int) {
        realTimeSeekBarProgress = value
        realTime.value = getRealTimeFromSeekBarProgress(value)
    }

    fun getRealTime(): LiveData<Int> = realTime

    fun setSimTimeSeekBarProgress(value: Int) {
        simTimeSeekBarProgress = value
        simTime.value = getSimTimeFromSeekBarProgress(value)
    }

    fun getSimTime(): LiveData<Int> = simTime

    fun setHomeTeam(value: String) {
        homeTeam.value = value
    }

    fun getHomeTeam(): LiveData<String> = homeTeam

    fun setAwayTeam(value: String) {
        awayTeam.value = value
    }

    fun getAwayTeam(): LiveData<String> = awayTeam

    fun setHomeScore(value: Int) {
        homeScore.value = value
    }

    fun getHomeScore(): LiveData<Int> = homeScore

    fun setAwayScore(value: Int) {
        awayScore.value = value
    }

    fun getAwayScore(): LiveData<Int> = awayScore

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