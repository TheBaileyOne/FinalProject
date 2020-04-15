package com.example.studentlifeapp.util

import com.example.studentlifeapp.data.Classification
import com.example.studentlifeapp.data.Event
import java.lang.Exception

class Utils {
    interface EventDetailClickListener{
        fun onEventClicked(tag:String, event:Event)
    }


}
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
