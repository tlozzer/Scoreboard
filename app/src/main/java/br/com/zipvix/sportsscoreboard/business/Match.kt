package br.com.zipvix.sportsscoreboard.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.zipvix.sportsscoreboard.model.Timer

class Match(
    private val realHalfTimeInMinutes: Int,
    private val simulatedHalfTimeInMinutes: Int,
    private val hasTwoHalf: Boolean = true,
    private val eventListener: MatchEventListener? = null
) {
    private val _homeScore = MutableLiveData(0)
    private val _awayScore = MutableLiveData(0)
    private val _status = MutableLiveData<Status>(Status.STOPPED)
    private val _currentHalf = MutableLiveData(0)

    val homeScore: LiveData<Int>
        get() = _homeScore

    val awayScore: LiveData<Int>
        get() = _awayScore

    val status: LiveData<Status>
        get() = _status

    val currentHalf: LiveData<Int>
        get() = _currentHalf

    val timeToFinish: LiveData<Long>
        get() = Timer.getMillisUntilFinish()

    fun start() {
        _currentHalf.value = _currentHalf.value?.plus(1)
        if ((_currentHalf.value ?: 0) > 1 && !hasTwoHalf)
            return
        if ((_currentHalf.value ?: 0) > 2)
            return

        eventListener?.also {
            when (_currentHalf.value) {
                1 -> it.beforeStartMatch()
                2 -> it.beforeStartSecondHalf()
            }
        }

        Timer.start(realHalfTimeInMinutes * 60 * 1000L, simulatedHalfTimeInMinutes * 60 * 1000L) {
            eventListener?.also {
                when (_currentHalf.value) {
                    1 -> it.beforeEndOfFirstHalf()
                    2 -> it.beforeEndOfMatch()
                }
            }
            _status.value = Status.FINISHING

            _status.value = Status.FINISHED
            eventListener?.also {
                when (_currentHalf.value) {
                    1 -> it.onEndOfFirstHalf()
                    2 -> it.onEndOfMatch()
                }
            }
        }

        eventListener?.also {
            when (_currentHalf.value) {
                1 -> it.onStartMatch()
                2 -> it.onStartSecondHalf()
            }
        }

        _homeScore.value = 0
        _awayScore.value = 0
        _status.value = Status.STOPPED
        _status.value = Status.RUNNING
    }

    fun addHomeScore() {
        _homeScore.value = (_homeScore.value ?: 0) + 1
    }

    fun addAwayScore() {
        _awayScore.value = (_awayScore.value ?: 0) + 1
    }

    enum class Status { STOPPED, RUNNING, FINISHING, FINISHED }

    interface MatchEventListener {
        fun beforeStartMatch()
        fun onStartMatch()
        fun beforeEndOfFirstHalf()
        fun onEndOfFirstHalf()
        fun beforeStartSecondHalf()
        fun onStartSecondHalf()
        fun beforeEndOfMatch()
        fun onEndOfMatch()
    }
}