package br.com.zipvix.sportsscoreboard.model

import android.os.CountDownTimer

class Timer(millisInFuture: Long, countDownInterval: Long) :
    CountDownTimer(millisInFuture, countDownInterval) {

    var status: Status = Status.STOPPED
        private set

    override fun onFinish() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTick(milisUntilFinished: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    enum class Status {
        STOPPED, STARTED, PAUSED
    }
}