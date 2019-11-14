package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import br.com.zipvix.sportsscoreboard.model.TeamListModel
import br.com.zipvix.sportsscoreboard.model.Timer
import br.com.zipvix.sportsscoreboard.repository.FirestoreRepository
import br.com.zipvix.sportsscoreboard.repository.entity.Team

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirestoreRepository()

    private var realTimeSeekBarProgress: Int = 2
    private var simTimeSeekBarProgress: Int = 4
    private val realTime =
        MutableLiveData<Long>(getRealTimeFromSeekBarProgress(realTimeSeekBarProgress))
    private val simTime =
        MutableLiveData<Long>(getSimTimeFromSeekBarProgress(simTimeSeekBarProgress))
    private val timeToFinish = MediatorLiveData<Long>()
    private val homeTeam = MutableLiveData<Team>()
    private val awayTeam = MutableLiveData<Team>()
    private val homeScore = MutableLiveData(0)
    private val awayScore = MutableLiveData(0)
    private val status = MutableLiveData<Status>(Status.STOPPED)
    private val teams = MutableLiveData<List<Team>>()

    init {
        timeToFinish.let {
            it.addSource(Timer.getMillisUntilFinish()) { value ->
                it.value = value
            }
            it.addSource(simTime) { value ->
                it.value = value * 60 * 1000
            }
        }
    }

    fun loadTeams() {
        repository.listTeams { teams ->
            this.teams.value = teams
        }
    }

    fun getTeams(): LiveData<List<Team>> = teams

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

    fun setHomeTeam(value: Team) {
        homeTeam.value = value
    }

    fun getHomeTeam(): LiveData<Team> = homeTeam

    fun setAwayTeam(value: Team) {
        awayTeam.value = value
    }

    fun getAwayTeam(): LiveData<Team> = awayTeam

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
        Timer.start((realTime.value ?: 0) * 60 * 1000, (simTime.value ?: 0) * 60 * 1000) {
            status.value = Status.STOPPED
        }
        homeScore.value = 0
        awayScore.value = 0
        status.value = Status.RUNNING
    }

    fun getTimeInMillisToFinish(): LiveData<Long> = timeToFinish

    fun getStatus(): LiveData<Status> = status

    enum class Status {
        STOPPED, RUNNING, PAUSED
    }
}