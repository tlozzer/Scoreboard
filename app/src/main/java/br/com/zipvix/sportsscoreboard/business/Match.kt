package br.com.zipvix.sportsscoreboard.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.zipvix.sportsscoreboard.model.Timer
import br.com.zipvix.sportsscoreboard.repository.entity.Team

class Match(
    val homeTeam: Team,
    val awayTeam: Team,
    val realHalfTimeInMinutes: Int,
    val simulatedHalfTimeInMinutes: Int,
    val twoHalfs: Boolean = true
) {
    private val homeScore = MutableLiveData<Int>(0)
    private val awayScore = MutableLiveData<Int>(0)
    private val status = MutableLiveData<Status>(Status.STOPPED)
    val liveStatus: LiveData<Status> = status
    val liveHomeScore: LiveData<Int> = homeScore
    val liveAwayScore: LiveData<Int> = awayScore


    fun start() {
        Timer.start(realHalfTimeInMinutes * 60 * 1000L, simulatedHalfTimeInMinutes * 60 * 1000L) {
            status.value = Status.FINISHING
            status.value = Status.FINISHED
        }
        status.value = Status.RUNNING
    }

    enum class Status { STOPPED, RUNNING, FINISHING, FINISHED }
}