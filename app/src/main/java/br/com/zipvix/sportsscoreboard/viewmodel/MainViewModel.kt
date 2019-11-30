package br.com.zipvix.sportsscoreboard.viewmodel

import android.app.Application
import androidx.lifecycle.*
import br.com.zipvix.sportsscoreboard.business.Match
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

    private var _match: Match =
        Match(_realTime.value?.toInt() ?: 0, _simTime.value?.toInt() ?: 0, twoHalf)

    private val _teams = MutableLiveData<List<Team>>()

    val teams: LiveData<List<Team>>
        get() = _teams

    private val _canStart = MediatorLiveData<Boolean>().apply {
        this.addSource(_homeTeam) {
            this.value = _awayTeam.value != null
        }
        this.addSource(_awayTeam) {
            this.value = _homeTeam.value != null
        }
    }

    val canStartMatch: LiveData<Boolean>
        get() = _canStart

    val currentHalf = Transformations.map(_match.currentHalf) { it }

    val timeToFinish = Transformations.map(_match.timeToFinish) { it }

    val status = Transformations.map(_match.status) { it }

    val liveHomeScore: LiveData<Int>
        get() = Transformations.map(_match.homeScore) { it }

    val liveAwayScore: LiveData<Int>
        get() = Transformations.map(_match.awayScore) { it }

    init {
        _repository.listTeams { teamsList ->
            _teams.value = teamsList
        }
    }

    override fun onCleared() {
        super.onCleared()
        _canStart.removeSource(_homeTeam)
        _canStart.removeSource(_awayTeam)
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
        _match.addHomeScore()
    }

    fun addAwayScore() {
        _match.addAwayScore()
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
        _canStart.value?.also {
            if (it) {
                _match = Match(_realTime.value?.toInt() ?: 0, _simTime.value?.toInt() ?: 0, twoHalf)
                _match.start()
            }
        }
    }
}