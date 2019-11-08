package br.com.zipvix.sportsscoreboard.model

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Timer(realTimeInMinutes: Long) :
    CountDownTimer(realTimeInMinutes * 60 * 1000, 1000) {

    private val millisUntilFinish = MutableLiveData<Long>(realTimeInMinutes * 60 * 1000)

    override fun onFinish() {
    }

    override fun onTick(millisUntilFinished: Long) {
        millisUntilFinish.value = millisUntilFinished
    }

    fun getMillisUntilFinish(): LiveData<Long> = millisUntilFinish
}