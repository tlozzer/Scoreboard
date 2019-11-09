package br.com.zipvix.sportsscoreboard.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Timer {

    private var timer: CountDownTimer? = null
    private val millisUntilFinished = MutableLiveData<Long>(0L)

    fun start(millisInFuture: Long, countDownInterval: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onFinish() {
            }

            override fun onTick(millisUntilFinished: Long) {
                this@Timer.millisUntilFinished.value = millisUntilFinished
            }

        }
        timer?.start()
    }

    fun getMillisUntilFinish(): LiveData<Long> = millisUntilFinished
}