package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import br.com.zipvix.sportsscoreboard.model.Timer

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var realTimeSeekBarProgress: Int = 2
    private var simTimeSeekBarProgress: Int = 4
    private val realTime =
        MutableLiveData<Long>(getRealTimeFromSeekBarProgress(realTimeSeekBarProgress))
    private val simTime =
        MutableLiveData<Long>(getSimTimeFromSeekBarProgress(simTimeSeekBarProgress))
    private val timeToFinish = MediatorLiveData<Long>()
    private val homeTeam = MutableLiveData<String>("")
    private val awayTeam = MutableLiveData<String>("")
    private val homeScore = MutableLiveData(0)
    private val awayScore = MutableLiveData(0)

    init {
        timeToFinish.let {
            it.addSource(Timer.getMillisUntilFinish(), Observer { value ->
                it.value = value
            })
            it.addSource(realTime, Observer { value ->
                it.value = value * 60 * 1000
            })
        }
    }

    fun setRealTimeSeekBarProgress(value: Int) {
        realTimeSeekBarProgress = value
        realTime.value = getRealTimeFromSeekBarProgress(value)
    }

    fun getRealTime(): LiveData<Long> = realTime

    fun setSimTimeSeekBarProgress(value: Int) {
        simTimeSeekBarProgress = value
        simTime.value = getSimTimeFromSeekBarProgress(value)
    }

    fun getSimTime(): LiveData<Long> = simTime

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

    private fun getRealTimeFromSeekBarProgress(progress: Int): Long {
        return when (progress) {
            0 -> 3
            1 -> 5
            2 -> 10
            3 -> 15
            else -> 20
        }
    }

    private fun getSimTimeFromSeekBarProgress(progress: Int): Long {
        return when (progress) {
            0 -> 5
            1 -> 10
            2 -> 20
            3 -> 30
            else -> 45
        }
    }

    fun start() {
        Timer.start((realTime.value ?: 0) * 60 * 1000, 1000)
        homeScore.value = 0
        awayScore.value = 0
    }

    fun getTimeInMillisToFinish(): LiveData<Long> = timeToFinish

}