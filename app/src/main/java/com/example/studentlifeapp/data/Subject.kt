package com.example.studentlifeapp.data

import kotlinx.android.parcel.RawValue
import org.threeten.bp.LocalDateTime
import java.io.Serializable

class Subject(val name: String, val summary: String, val events:MutableList<Event> = mutableListOf(),val subjectStart: LocalDateTime = LocalDateTime.now(),val subjectEnd:LocalDateTime = subjectStart.plusMonths(1)){
    fun addEvents(newEvents: MutableList<Event>){
        for (event in newEvents){
            events.add(event)
        }
    }

}
