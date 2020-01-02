package com.example.studentlifeapp.data

import android.icu.text.CaseMap
import androidx.annotation.ColorInt
import com.example.studentlifeapp.R
import org.threeten.bp.LocalDateTime


//interface EventInterface {
//    val title: String
//    val type: EventType
//    val startTime: LocalDateTime
//    val notifications: MutableList<LocalDateTime>
//
//    fun getColour(): Int
//}
////TODO: make it possible to edit values, and restructure class for efficiency
//data class Reminder(override val title: String, override val startTime: LocalDateTime,
//                    override val type: EventType = EventType.REMINDER,
//                    override val notifications: MutableList<LocalDateTime> = mutableListOf(startTime)):EventInterface{
//    override fun getColour(): Int {
//        return R.color.Red
//    }
//}
//
//data class Event(override val title: String, override val type: EventType,
//                 override val startTime: LocalDateTime, val endTime : LocalDateTime = startTime.plusHours(1),
//                 override val notifications: MutableList<LocalDateTime> = mutableListOf(startTime.minusHours(1)),
//                 val location: Location?):EventInterface{
//    override fun getColour(): Int {
//        TODO("set correct colours")
//        return R.color.SkyBlue
//    }
//}


data class Event(val title: String, val type: EventType = EventType.EVENT, val startTime: LocalDateTime, val endTime : LocalDateTime = startTime,
                 val notifications: MutableList<LocalDateTime> = mutableListOf(startTime), val location: Location? = null, val note: String = ""){
    val colour: Int = setColour()
    fun setColour(): Int {
        //TODO("set correct colours")
        return when (type) {
            EventType.LECTURE ->  R.color.SkyBlue
            EventType.REMINDER ->  R.color.Red
            EventType.CLASS ->  R.color.ForestGreen
            EventType.STUDY ->  R.color.Goldenrod
            EventType.EXAM ->  R.color.Tomato
            EventType.COURSEWORK ->  R.color.Blue
            EventType.EVENT -> R.color.White
            EventType.JOBSHIFT -> R.color.MediumPurple
            EventType.SOCIETY -> R.color.DeepPink
        }
    }


}

data class Location(val name:String, val town: String? = null, val city: String? = null, val county: String? = null, val  postCode: String? = null, val country: String= "United Kingdom"){
    fun basicDisplay()= "$name, $country"
    fun fullDisplay()="$name, " + {if (town != null) "$town, "} + {if (city != null) "$city, "} + {if (county != null)"$county, "} + { if (postCode!=null)"postCode, "} +"$country."
}

enum class EventType{
    LECTURE, CLASS, STUDY, EXAM, COURSEWORK, JOBSHIFT, SOCIETY, REMINDER, EVENT
}

