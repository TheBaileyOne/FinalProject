package com.example.studentlifeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studentlifeapp.activities.StudyMode
import com.example.studentlifeapp.data.AppConstants
import com.example.studentlifeapp.util.NotificationUtil
import com.example.studentlifeapp.util.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        when(intent.action){
            AppConstants.ACTION_STOP -> {
                StudyMode.removeAlarm(context)
                PrefUtil.setTimerState(StudyMode.TimerState.STOPPED, context)
                NotificationUtil.hideTimerNotification(context)
            }
            AppConstants.ACTION_PAUSE ->{
                var secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val nowSeconds = StudyMode.nowSeconds

                secondsRemaining -= nowSeconds-alarmSetTime //time alarm was running in the background
                PrefUtil.setSecondsRemaining(secondsRemaining, context)

                StudyMode.removeAlarm(context)
                PrefUtil.setTimerState(StudyMode.TimerState.PAUSED, context)
                NotificationUtil.showTimerPaused(context)
            }
            AppConstants.ACTION_RESUME -> {
                val secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val wakeUpTime = StudyMode.setAlarm(context, StudyMode.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(StudyMode.TimerState.RUNNING, context)
                NotificationUtil.showTimerRunning(context,wakeUpTime)
            }
            AppConstants.ACTION_START -> {
                val minutesRemaining = PrefUtil.getTimerLength(context)
                val secondsRemaining = minutesRemaining * 60L
                val wakeUpTime = StudyMode.setAlarm(context, StudyMode.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(StudyMode.TimerState.RUNNING, context)
                PrefUtil.setSecondsRemaining(secondsRemaining, context)
                NotificationUtil.showTimerRunning(context,wakeUpTime)
            }
        }
    }
}
