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
    private val homeScore = MutableLiveData(0)
    private val awayScore = MutableLiveData(0)
    private val status = MutableLiveData<Status>(Status.STOPPED)
    private val currentHalf = MutableLiveData(0)

    fun getStatus(): LiveData<Status> = status

    fun getHomeScore(): LiveData<Int> = homeScore

    fun getAwayScore(): LiveData<Int> = awayScore

    fun getCurrentHalf(): LiveData<Int> = currentHalf

    fun getTimeToFinish(): LiveData<Long> = Timer.getMillisUntilFinish()

    fun start() {
        currentHalf.value = currentHalf.value?.plus(1)
        if ((currentHalf.value ?: 0) > 1 && !hasTwoHalf)
            return
        if ((currentHalf.value ?: 0) > 2)
            return

        eventListener?.also {
            when (currentHalf.value) {
                1 -> it.beforeStartMatch()
                2 -> it.beforeStartSecondHalf()
            }
        }

        Timer.start(realHalfTimeInMinutes * 60 * 1000L, simulatedHalfTimeInMinutes * 60 * 1000L) {
            eventListener?.also {
                when (currentHalf.value) {
                    1 -> it.beforeEndOfFirstHalf()
                    2 -> it.beforeEndOfMatch()
                }
            }
            status.value = Status.FINISHING

            status.value = Status.FINISHED
            eventListener?.also {
                when (currentHalf.value) {
                    1 -> it.onEndOfFirstHalf()
                    2 -> it.onEndOfMatch()
                }
            }
        }

        eventListener?.also {
            when (currentHalf.value) {
                1 -> it.onStartMatch()
                2 -> it.onStartSecondHalf()
            }
        }

        status.value = Status.STOPPED
        status.value = Status.RUNNING
    }

    fun setHomeScore(score: Int) {
        homeScore.value = score
    }

    fun setAwayScore(score: Int) {
        awayScore.value = score
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