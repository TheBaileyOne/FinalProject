package com.example.studentlifeapp.data

import com.example.studentlifeapp.R
import org.threeten.bp.LocalDateTime
import java.io.Serializable


data class Event(
    var title: String,
    var type: EventType = EventType.EVENT,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime = startTime,
    var notifications: MutableList<LocalDateTime> = mutableListOf(startTime),
    var location: Location? = null,
    var note: String? = null,
//    val times: MutableList<LocalDateTime> = mutableListOf(startTime,endTime),
    var eventId: String = title,
    var eventRef:String = ""
): Serializable
{
    var colour: Int = setColour()
    private fun setColour(): Int {
        return when (type) {
            EventType.LECTURE ->  R.color.SkyBlue
            EventType.REMINDER ->  R.color.Red
            EventType.CLASS ->  R.color.ForestGreen
            EventType.STUDY ->  R.color.Goldenrod
            EventType.EXAM ->  R.color.Tomato
            EventType.COURSEWORK ->  R.color.Blue
            EventType.EVENT -> R.color.Black
            EventType.JOBSHIFT -> R.color.MediumPurple
            EventType.SOCIETY -> R.color.DeepPink
        }
    }
    private fun setCustomColour(colour: Int) {
        this.colour = colour
    }

    fun setRef(ref:String){
        eventRef = ref
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

