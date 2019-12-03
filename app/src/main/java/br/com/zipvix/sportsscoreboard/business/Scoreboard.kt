package br.com.zipvix.sportsscoreboard.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

object Scoreboard {
    private val mutableHomeScore = MutableLiveData(0)
    private val mutableAwayScore = MutableLiveData(0)
    private val mutableStatus = MutableLiveData<Status>(Status.STOPPED)
    private val mutableCurrentPeriod = MutableLiveData(0)

    init {
        mutableHomeScore.value = 0
        mutableAwayScore.value = 0
        mutableStatus.value = Status.STOPPED
        mutableCurrentPeriod.value = 0
    }

    val homeScore: LiveData<Int>
        get() = mutableHomeScore

    val awayScore: LiveData<Int>
        get() = mutableAwayScore

    val status: LiveData<Status>
        get() = mutableStatus

    val currentPeriod: LiveData<Int>
        get() = mutableCurrentPeriod

    val timeToFinish: LiveData<Long>
        get() = Transformations.map(Timer.getMillisUntilFinish()) { it }

    fun start(
        realHalfTimeInMinutes: Int,
        simulatedHalfTimeInMinutes: Int,
        hasTwoHalf: Boolean = true,
        eventListener: MatchEventListener? = null
    ) {
        mutableCurrentPeriod.value = getNextPeriod(mutableCurrentPeriod.value ?: 0, hasTwoHalf)
        if (mutableCurrentPeriod.value == 1) {
            mutableHomeScore.value = 0
            mutableAwayScore.value = 0
        }

        var adjustedRealHalfTimeInMinutes = 0L
        var adjustedSimulatedTimeInMinutes = 0L
        when (mutableCurrentPeriod.value ?: 0) {
            1, 2 -> {
                adjustedRealHalfTimeInMinutes = realHalfTimeInMinutes.toLong()
                adjustedSimulatedTimeInMinutes = simulatedHalfTimeInMinutes.toLong()
            }
            3, 4 -> {
                adjustedRealHalfTimeInMinutes = realHalfTimeInMinutes.toLong() / 3
                adjustedSimulatedTimeInMinutes = simulatedHalfTimeInMinutes.toLong() / 3
            }
        }

        eventListener?.also {
            when (mutableCurrentPeriod.value) {
                1 -> it.beforeStartMatch()
                2 -> it.beforeStartSecondHalf()
                3 -> it.beforeStartExtraTime()
                4 -> it.beforeStartExtraTimeSecondHalf()
                5 -> it.beforeStartPenaltyKicks()
            }
        }

        Timer.start(adjustedRealHalfTimeInMinutes * 60 * 1000L, adjustedSimulatedTimeInMinutes * 60 * 1000L) {
            eventListener?.also {
                when (mutableCurrentPeriod.value) {
                    1 -> it.beforeEndOfFirstHalf()
                    2 -> it.beforeEndOfSecondHalf()
                    3 -> it.beforeEndOfExtraTimeFirstHalf()
                    4 -> it.beforeEndOfExtraTimeSecondHalf()
                }
            }
            mutableStatus.value = Status.FINISHING

            mutableStatus.value = Status.FINISHED
            eventListener?.also {
                when (mutableCurrentPeriod.value) {
                    1 -> it.onEndOfFirstHalf()
                    2 -> it.onEndOfSecondHalf()
                    3 -> it.onEndOfExtraTimeFirstHalf()
                    4 -> it.onEndOfExtraTimeSecondHalf()
                }
            }
        }

        eventListener?.also {
            when (mutableCurrentPeriod.value) {
                1 -> it.onStartMatch()
                2 -> it.onStartSecondHalf()
                3 -> it.onStartExtraTimeFirstHalf()
                4 -> it.onStartExtraTimeSecondHalf()
                5 -> it.onStartPenaltyKicks()
            }
        }

        mutableStatus.value = Status.STOPPED
        mutableStatus.value = Status.RUNNING
    }

    private fun getNextPeriod(
        currentHalf: Int,
        hasTwoHalf: Boolean = true,
        hasExtraTime: Boolean = false,
        hasPenaltyKicks: Boolean = false
    ): Int {
        return if (currentHalf == 1 && hasTwoHalf) {
            2
        } else if (currentHalf == 1 && hasExtraTime) {
            3
        } else if (currentHalf == 1 && hasPenaltyKicks) {
            5
        } else if (currentHalf == 2 && hasExtraTime) {
            3
        } else if (currentHalf == 2 && hasPenaltyKicks) {
            5
        } else if (currentHalf == 3 && hasTwoHalf) {
            4
        } else if (currentHalf == 3 && hasPenaltyKicks) {
            5
        } else if (currentHalf == 4) {
            5
        } else {
            1
        }
    }

    fun addHomeScore() {
        mutableHomeScore.value = (mutableHomeScore.value ?: 0) + 1
    }

    fun addAwayScore() {
        mutableAwayScore.value = (mutableAwayScore.value ?: 0) + 1
    }

    enum class Status { STOPPED, RUNNING, FINISHING, FINISHED }

    interface MatchEventListener {
        fun beforeStartMatch()
        fun onStartMatch()
        fun beforeEndOfFirstHalf()
        fun onEndOfFirstHalf()
        fun beforeStartSecondHalf()
        fun onStartSecondHalf()
        fun beforeEndOfSecondHalf()
        fun onEndOfSecondHalf()
        fun beforeStartExtraTime()
        fun beforeStartExtraTimeSecondHalf()
        fun beforeStartPenaltyKicks()
        fun beforeEndOfExtraTimeFirstHalf()
        fun beforeEndOfExtraTimeSecondHalf()
        fun onEndOfExtraTimeFirstHalf()
        fun onEndOfExtraTimeSecondHalf()
        fun onStartExtraTimeFirstHalf()
        fun onStartExtraTimeSecondHalf()
        fun onStartPenaltyKicks()
    }
}