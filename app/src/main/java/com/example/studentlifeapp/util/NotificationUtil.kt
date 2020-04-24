package com.example.studentlifeapp.util

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.BLUE
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.studentlifeapp.BuildConfig
import com.example.studentlifeapp.R
import com.example.studentlifeapp.TimerNotificationActionReceiver
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.activities.StudyMode
import com.example.studentlifeapp.data.AppConstants
import com.google.firebase.firestore.util.AsyncQueue
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    //companion object allow to call from anywhere without instance
    companion object {
        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val CHANNEL_NAME_TIMER  = "Study Mode Timer"
        //Have to use notification channels if building for oreo or later
        private const val TIMER_ID = 0


        fun showTimerExpired(context: Context){
            val startIntent = Intent(context,TimerNotificationActionReceiver::class.java )
            startIntent.action = AppConstants.ACTION_START
            val startPendingIntent = PendingIntent.getBroadcast(context,0,
                    startIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            val pendingIntent = PendingIntent.getActivity(context, 0,
                newLauncherIntent(context),PendingIntent.FLAG_UPDATE_CURRENT)
            notificationBuilder.setContentTitle("Study Finished!")
                .setContentText("Start again?")
                .setContentIntent(getPendingIntentWithStack(context, StudyMode::class.java)) // when user clicks on notifcation will go to correct activity
//                .setContentIntent(pendingIntent) // when user clicks on notifcation will go to correct activity
                .addAction(R.drawable.ic_play, "Start", startPendingIntent)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            notificationManager.notify(TIMER_ID, notificationBuilder.build())

        }

        fun showTimerRunning(context: Context, wakeUpTime: Long){
            val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            stopIntent.action = AppConstants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(context,
                    0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            pauseIntent.action = AppConstants.ACTION_PAUSE
            val pausePendingIntent = PendingIntent.getBroadcast(context,
                0, pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val dateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
            val pendingIntent = PendingIntent.getActivity(context, 0,
                newLauncherIntent(context),PendingIntent.FLAG_UPDATE_CURRENT)
//            val pendingIntent = PendingIntent.getActivity(context, 0,context.packageManager
//                .getLaunchIntentForPackage(BuildConfig.APPLICATION_ID),PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            notificationBuilder.setContentTitle("Studying")
                .setContentText("End: ${dateFormat.format(Date(wakeUpTime))}")
//                .setContentIntent(pendingIntent) // when user clicks on notifcation will go to correct activity
                .setContentIntent(getPendingIntentWithStack(context, StudyMode::class.java)) // when user clicks on notifcation will go to correct activity
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, false)
            notificationManager.notify(TIMER_ID, notificationBuilder.build())

        }
        //TODO::Edit this to be a countdown for a break
        fun showTimerPaused(context: Context){
            val resumeIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            resumeIntent.action = AppConstants.ACTION_RESUME
            val resumePendingIntent = PendingIntent.getBroadcast(context,
                0 , resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
            val pendingIntent = PendingIntent.getActivity(context, 0,
                newLauncherIntent(context),PendingIntent.FLAG_UPDATE_CURRENT)
            notificationBuilder.setContentTitle("Study paused")
                .setContentText("Resume?")
//                .setContentIntent(pendingIntent)
                .setContentIntent(getPendingIntentWithStack(context, StudyMode::class.java)) // when user clicks on notifcation will go to correct activity
                .setOngoing(true)
                .addAction(R.drawable.ic_play, "Resume", resumePendingIntent)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            notificationManager.notify(TIMER_ID, notificationBuilder.build())

        }

//        fun recommendBreak(context:Context){
//            val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
//            pauseIntent.action = AppConstants.ACTION_PAUSE
//            val pausePendingIntent = PendingIntent.getBroadcast(context,
//                0, pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT)
//            val notificationBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
//            val pendingIntent = PendingIntent.getActivity(context, 0,
//                newLauncherIntent(context),PendingIntent.FLAG_UPDATE_CURRENT)
//            notificationBuilder.setContentTitle("Take a break!")
//                .setContentText("Take a five minute break, stretch, grab some water?!")
////                .setContentIntent(pendingIntent)
//                .setContentIntent(getPendingIntentWithStack(context, StudyMode::class.java))
//                .setOngoing(true)
//                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
//            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(CHANNEL_ID_TIMER, "Study Break", true)
//            notificationManager.notify(TIMER_ID,notificationBuilder.build())
//        }

        fun hideTimerNotification(context: Context){
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(TIMER_ID)
        }

        private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean): NotificationCompat.Builder {
            val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context,channelId)
//                .setSmallIcon(R.drawable.ic_timer)
                .setSmallIcon(R.drawable.app_logo)
                .setAutoCancel(true)
                .setDefaults(0)
            if (playSound) notificationBuilder.setSound(notificationSound)
            return notificationBuilder
        }

        private fun <T> getPendingIntentWithStack(context: Context,javaClass: Class <T>):PendingIntent{
            val resultIntent = Intent(context, javaClass)
//            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(resultIntent)
//            stackBuilder.addParentStack(javaClass)
//            stackBuilder.addNextIntent(resultIntent)//activity we want to open

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(channelID:String, channelName:String,
                                playSound:Boolean){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                val channelImportance = if(playSound) NotificationManager.IMPORTANCE_DEFAULT
                                            else NotificationManager.IMPORTANCE_LOW
                val notificationChannel = NotificationChannel(channelID, channelName, channelImportance)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = BLUE
                this.createNotificationChannel(notificationChannel)
            }

        }

    }

}