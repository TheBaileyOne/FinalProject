package com.example.studentlifeapp.data

import com.example.studentlifeapp.R
import org.threeten.bp.LocalDateTime
import java.io.Serializable


data class Event(
    val title: String,
    val type: EventType = EventType.EVENT,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime = startTime,
    val notifications: MutableList<LocalDateTime> = mutableListOf(startTime),
    val location: Location? = null,
    val note: String = "",
    val times: MutableList<LocalDateTime> = mutableListOf(startTime,endTime)): Serializable
{
    val colour: Int = setColour()
    private fun setColour(): Int {
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
    //TODO: add change location to include building room and stuff.
    fun basicDisplay()= "$name, $country"
    fun fullDisplay()="$name, " + {if (town != null) "$town, "} + {if (city != null) "$city, "} + {if (county != null)"$county, "} + { if (postCode!=null)"postCode, "} +"$country."
}

enum class EventType{
    LECTURE, CLASS, STUDY, EXAM, COURSEWORK, JOBSHIFT, SOCIETY, REMINDER, EVENT
}

