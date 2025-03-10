package com.example.studentlifeapp.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.studentlifeapp.activities.StudyMode


class PrefUtil{
    /**
     * Object sets preferences for Timer
     */
    companion object{
        private const val TIMER_LENGTH_ID = "com.example.studentlifeapp.timer_length"
        //length in minutes
        fun getTimerLength(context: Context):Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID, 15)
        }

        fun setTimerLength(duration:Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(TIMER_LENGTH_ID, duration)
            editor.apply()
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.example.studentlifeapp.previous_timer_length"


        fun getPreviousTimerLengthSeconds(context:Context):Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,0)
        }

        fun setPreviousTimerLengthSeconds(seconds:Long, context:Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.example.studentlifeapp.timer_state"

        fun getTimerState(context: Context): StudyMode.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID,0)
            return StudyMode.TimerState.values()[ordinal]
        }

        fun setTimerState(state:StudyMode.TimerState,context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID,ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "com.example.studentlifeapp.seconds_remaining"
        fun getSecondsRemaining(context:Context):Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID,0)
        }
        fun setSecondsRemaining(seconds:Long, context:Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.example.studentlifeapp.background_time"

        fun getAlarmSetTime(context:Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID,0)
        }

        fun setAlarmSetTime(time: Long, context:Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}