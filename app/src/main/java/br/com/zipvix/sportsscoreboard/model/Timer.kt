package br.com.zipvix.sportsscoreboard.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Timer {

    private var timer: CountDownTimer? = null
    private val millisUntilFinished = MutableLiveData<Long>(0L)

    fun start(millisInFuture: Long, simulatedMillisInFuture: Long, executeOnFinish: () -> Unit) {
        val fraction = millisInFuture.toDouble() / simulatedMillisInFuture
        timer?.cancel()
        timer = object : CountDownTimer(millisInFuture, (fraction * 1000).toLong()) {
            override fun onFinish() {
                executeOnFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                this@Timer.millisUntilFinished.value = (millisUntilFinished / fraction).toLong()
            }

        }
        timer?.start()
    }

    fun getMillisUntilFinish(): LiveData<Long> = millisUntilFinished
}