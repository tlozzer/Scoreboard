package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import br.com.zipvix.sportsscoreboard.business.Scoreboard
import br.com.zipvix.sportsscoreboard.repository.FirestoreRepository
import br.com.zipvix.sportsscoreboard.repository.entity.Team

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _repository = FirestoreRepository()

    var realTimeSeekBarProgress: Int = 2
        set(value) {
            field = value
            _realTime.value = getRealTimeFromSeekBarProgress(value)
        }

    var simTimeSeekBarProgress: Int = 4
        set(value) {
            field = value
            _simTime.value = getSimTimeFromSeekBarProgress(value)
        }

    private val _realTime =
        MutableLiveData<Long>(getRealTimeFromSeekBarProgress(realTimeSeekBarProgress))

    val realTime: LiveData<Long>
        get() = _realTime

    private val _simTime =
        MutableLiveData<Long>(getSimTimeFromSeekBarProgress(simTimeSeekBarProgress))

    val simTime: LiveData<Long>
        get() = _simTime

    private val _homeTeam = MutableLiveData<Team>()

    val homeTeam: LiveData<Team>
        get() = _homeTeam

    private val _awayTeam = MutableLiveData<Team>()

    val awayTeam: LiveData<Team>
        get() = _awayTeam

    var twoHalf = true

    private val _teams = MutableLiveData<List<Team>>()

    val teams: LiveData<List<Team>>
        get() = _teams

    val currentHalf: LiveData<Int>
        get() = Transformations.map(Scoreboard.currentPeriod) { it }

    val timeToFinish: LiveData<Long>
        get() = Transformations.map(Scoreboard.timeToFinish) { it }

    val status: LiveData<Scoreboard.Status>
        get() = Transformations.map(Scoreboard.status) { it }

    val homeScore: LiveData<Int>
        get() = Transformations.map(Scoreboard.homeScore) { it }

    val awayScore: LiveData<Int>
        get() = Transformations.map(Scoreboard.awayScore) { it }

    init {
        _repository.listTeams { teamsList ->
            _teams.value = teamsList
        }
    }

    fun setHomeTeam(team: Team) {
        if (team != _homeTeam.value) {
            _homeTeam.value = team
        }
    }

    fun setAwayTeam(team: Team) {
        if (team != _awayTeam.value) {
            _awayTeam.value = team
        }
    }

    fun addHomeScore() {
        Scoreboard.addHomeScore()
    }

    fun addAwayScore() {
        Scoreboard.addAwayScore()
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
        Scoreboard.start(_realTime.value?.toInt() ?: 0, _simTime.value?.toInt() ?: 0, twoHalf)
    }
}