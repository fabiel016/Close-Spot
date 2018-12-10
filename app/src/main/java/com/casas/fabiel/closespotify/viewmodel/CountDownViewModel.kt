package com.casas.fabiel.closespotify.viewmodel

import android.app.PendingIntent
import android.app.AlarmManager
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.TimePickerDialog
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import com.casas.fabiel.closespotify.extensions.toHoursTime
import com.casas.fabiel.closespotify.extensions.toMinutesTime
import com.casas.fabiel.closespotify.extensions.toSecondsTime
import com.casas.fabiel.closespotify.extensions.toTimeUnits
import com.casas.fabiel.closespotify.services.CloseServiceIntent
import java.util.*

class CountDownViewModel(val context: Context) : ViewModel() {
    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null
    lateinit var hours: MutableLiveData<String>
    lateinit var minute: MutableLiveData<String>
    lateinit var seconds: MutableLiveData<String>
    private var alarmTimeMillis = 0L

    init {
    }

    companion object {
        val TURN_OFF_REQUEST_CODE = 70
    }

    fun setTimer() {
        val timeDialog = TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> startAlarm(hourOfDay, minute) }, 0, 0, true)
        timeDialog.show()
    }

    fun startAlarm(hourOfDay: Int, minute: Int) {
        scheduleIntentAlarm(hourOfDay, minute)
        startCountDownClock()
    }

    private fun startCountDownClock() {
        val timerCount = alarmTimeMillis - Calendar.getInstance().timeInMillis
        object : CountDownTimer(timerCount, 500) {

            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished.toHoursTime()
                val minutes = millisUntilFinished.toMinutesTime()
                val second = millisUntilFinished.toSecondsTime()
                updateTimerText(hours, minutes, second)
            }

            override fun onFinish() {
                updateTimerText(0, 0, 0)
            }
        }.start()
    }

    private fun updateTimerText(hourOfDay: Int, minu: Int, sec: Int) {
        hours.value = hourOfDay.toTimeUnits()
        minute.value = minu.toTimeUnits()
        seconds.value = sec.toTimeUnits()
    }

    private fun scheduleIntentAlarm(hourOfDay: Int, minute: Int) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CloseServiceIntent::class.java)
        alarmIntent = PendingIntent.getService(context, TURN_OFF_REQUEST_CODE, intent, FLAG_UPDATE_CURRENT)
        val alarmTime = Calendar.getInstance()
        alarmTime.add(Calendar.HOUR, hourOfDay)
        alarmTime.add(Calendar.MINUTE, minute)
        alarmTimeMillis = alarmTime.timeInMillis
        alarmManager!!.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, alarmIntent)
    }

    fun getHours(): LiveData<String> {
        if (!::hours.isInitialized) {
            hours = MutableLiveData()
        }
        return hours
    }

    fun getMinuites(): LiveData<String> {
        if (!::minute.isInitialized) {
            minute = MutableLiveData()
        }
        return minute
    }

    fun getSeconds(): LiveData<String> {
        if (!::seconds.isInitialized) {
            seconds = MutableLiveData()
        }
        return seconds
    }

}