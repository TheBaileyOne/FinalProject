package com.example.studentlifeapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studentlifeapp.activities.StudyMode
import com.example.studentlifeapp.util.NotificationUtil
import com.example.studentlifeapp.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)
        //when timer finishes/expires it is stopped
        PrefUtil.setTimerState(StudyMode.TimerState.STOPPED, context)
        //when timer expires, alarm is notlonger set
        PrefUtil.setAlarmSetTime(0,context)

    }
}
