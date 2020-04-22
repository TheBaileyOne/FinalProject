package com.example.studentlifeapp.activities

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.studentlifeapp.R
import com.example.studentlifeapp.TimerExpiredReceiver
import com.example.studentlifeapp.util.NotificationUtil
import com.example.studentlifeapp.util.PrefUtil

import kotlinx.android.synthetic.main.activity_study_mode.*
import kotlinx.android.synthetic.main.content_study_mode.*
import java.util.*

class StudyMode : AppCompatActivity() {

    companion object{
        fun setAlarm(context: Context, nowSeconds:Long, secondsRemaining: Long): Long{
            val wakeupTime = (nowSeconds+secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,wakeupTime,pendingIntent)
            //remember time alarm set
            PrefUtil.setAlarmSetTime(nowSeconds,context)
            return wakeupTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0,context)

        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis /1000

    }

    enum class TimerState{
        STOPPED, PAUSED, RUNNING
    }
    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.STOPPED
    private var secondsRemaining = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val studyName = intent.getStringExtra("study_name")
        setContentView(R.layout.activity_study_mode)
        setSupportActionBar(toolbar)
//        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = " Study Mode${if(studyName != null)" : $studyName" else ""}"

        fab_start.setOnClickListener{
            startTimer()
            timerState = TimerState.RUNNING
            updateButtons()
        }
        fab_pause.setOnClickListener{
            timer.cancel()
            timerState = TimerState.PAUSED
            updateButtons()
        }
        fab_stop.setOnClickListener{
            timer.cancel()
            onTimerFinished()
        }
    }

    override fun onResume() {
        super.onResume()

        initTimer()

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.RUNNING){
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this,wakeUpTime)
        }
        else if (timerState == TimerState.PAUSED){
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds,this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)
        if (timerState == TimerState.STOPPED)
            setNewTimerLength()
        else
            setPreviousTimerLength()
        secondsRemaining = if(timerState==TimerState.RUNNING || timerState == TimerState.PAUSED)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if(alarmSetTime > 0) //if alarm is set
            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <=0)
            onTimerFinished() //called when timer finished in the background
        else if(timerState == TimerState.RUNNING)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished(){
        timerState=TimerState.STOPPED
        setNewTimerLength()
        progress_countdown.progress = 0
        PrefUtil.setSecondsRemaining(timerLengthSeconds,this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.RUNNING
        val context = this

        timer = object:CountDownTimer(secondsRemaining*1000, 1000){
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished/1000
                updateCountdownUI()
                val progress = (timerLengthSeconds - secondsRemaining).toInt()
                if(progress % 30 == 0){
                    NotificationUtil.recommendBreak(context)
                    Log.d("TAG", "Break Notification")
                }
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val duration = intent.getIntExtra("timer_length",0)
        Log.d("setting duration", "duration: $duration")
        val lengthInMinutes = if (duration > 0)duration
            else PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinutesUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinutesUntilFinished.toString()
        textView_countdown.text = "$minutesUntilFinished:${
        if(secondsStr.length == 2) secondsStr 
        else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when (timerState){
            TimerState.RUNNING -> {
                fab_start.isEnabled =false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.STOPPED -> {
                fab_start.isEnabled =true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.PAUSED -> {
                fab_start.isEnabled =true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Adds items to the action bar if present
        menuInflater.inflate(R.menu.menu_study_mode, menu)
        return true
    }

}
