package com.example.studentlifeapp.util

import com.example.studentlifeapp.data.Event

class Utils {
    interface EventDetailClickListener{
        fun onEventClicked(tag:String, event:Event)
    }

}
