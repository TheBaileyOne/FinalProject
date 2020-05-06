package com.example.studentlifeapp.util

import android.content.Context
import android.content.Intent
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.data.Classification
import com.example.studentlifeapp.data.Event


class Utils {
    interface EventDetailClickListener{
        fun onEventClicked(tag:String, event:Event)
    }


}

/**
 * Return the classificaition based on mark
 */
fun calculateClassification(percentage:Double): Classification {
    return if(percentage> 100){
        Classification.INVALID
    }
    else if (percentage in 70.0..100.0){
        Classification.FIRST
    }
    else if (percentage>=60){
        Classification.UPPER_SECOND
    }
    else if (percentage>=50){
        Classification.LOWER_SECOND
    }
    else if (percentage>=40){
        Classification.PASS
    }
    else if (percentage<40 && percentage>=0){
        Classification.FAIL
    }
    else{
        throw Exception("Invalid percentage")
    }
}

/**
 * Intent for launching activity from notification
 */
fun newLauncherIntent(context: Context): Intent? {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.action = Intent.ACTION_MAIN
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    return intent
}

