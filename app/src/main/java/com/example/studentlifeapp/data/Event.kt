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
    var note: String? = "",
//    val times: MutableList<LocalDateTime> = mutableListOf(startTime,endTime),
    var eventId: String = title,
    var eventRef:String = ""
): Serializable
{
    var colour: Int = setColour()
    //Set the colour for the event based on event type
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

    //For future imlementation  of setting own custom colour
    private fun setCustomColour(colour: Int) {
        this.colour = colour
    }
    //Set events database reference
    fun setRef(ref:String){
        eventRef = ref
    }

}


enum class EventType{
    LECTURE, CLASS, STUDY, EXAM, COURSEWORK, JOBSHIFT, SOCIETY, REMINDER, EVENT
}
//Wraps list of events for passing through intents
data class EventsParser(val events: MutableList<Event>)