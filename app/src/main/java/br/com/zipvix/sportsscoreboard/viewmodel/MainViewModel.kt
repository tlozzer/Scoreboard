package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.*
import br.com.zipvix.sportsscoreboard.business.Match
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
    private val homeTeam = MutableLiveData<Team>(Team())
    private val awayTeam = MutableLiveData<Team>(Team())
    var twoHalf = true
    private var match: Match =
        Match(realTime.value?.toInt() ?: 0, simTime.value?.toInt() ?: 0, twoHalf)
    private val teams = MutableLiveData<List<Team>>()
    private val canStart = MediatorLiveData<Boolean>().apply {
        this.addSource(homeTeam) {
            this.value = awayTeam.value != null
        }
        this.addSource(awayTeam) {
            this.value = homeTeam.value != null
        }
    }

    init {
        repository.listTeams { teamsList ->
            teams.value = teamsList
        }
    }

    val currentHalf = Transformations.map(match.currentHalf) { it }

    fun getStatus(): LiveData<Match.Status> = match.status

    fun getTimeToFinish(): LiveData<Long> = match.timeToFinish

    fun matchCanStart(): LiveData<Boolean> = canStart

    override fun onCleared() {
        super.onCleared()
        canStart.removeSource(homeTeam)
        canStart.removeSource(awayTeam)
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

    fun getHomeTeam(): LiveData<Team> = homeTeam
    fun setHomeTeam(team: Team) {
        if (team != homeTeam.value) {
            homeTeam.value = team
        }
    }

    fun getAwayTeam(): LiveData<Team> = awayTeam
    fun setAwayTeam(team: Team) {
        if (team != awayTeam.value) {
            awayTeam.value = team
        }
    }

    fun getHomeScore(): LiveData<Int> = match.homeScore
    fun addHomeScore() {
        match.addHomeScore()
    }

    fun getAwayScore(): LiveData<Int> = match.awayScore
    fun addAwayScore() {
        match.addAwayScore()
    }

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
        canStart.value?.also {
            if (it) {
                match = Match(realTime.value?.toInt() ?: 0, simTime.value?.toInt() ?: 0, twoHalf)
                match.start()
            }
        }
    }
}