package com.example.studentlifeapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import org.threeten.bp.LocalDateTime
//import org.threeten.bp.YearMonth

fun importEvents():MutableList<Event> {
    val list = mutableListOf<Event>()
//    val currentMonth = YearMonth.now()


    list.add(Event("Reminder1", EventType.REMINDER, LocalDateTime.now()))
    list.add(Event("Class1", EventType.CLASS, LocalDateTime.now().minusHours(2),
        LocalDateTime.now(),location = Location("Class Room")))
    list.add(Event("Lecture1", EventType.LECTURE, LocalDateTime.now(), LocalDateTime.now().plusHours(1),location = Location("Lecture Hall")))
    list.add(Event("Job Shift", EventType.JOBSHIFT, LocalDateTime.now().plusMinutes(90),
        LocalDateTime.now().plusHours(5),location = Location("Work Place")))
    list.add(Event("Event1", EventType.EVENT, LocalDateTime.now().plusDays(4),location = Location("Somewhere")))
    list.add(Event("Reminder2", EventType.REMINDER,LocalDateTime.now().plusHours(12)))
    list.add(Event("Reminder2", EventType.CLASS,LocalDateTime.now().plusHours(12), location = Location("class")))
    list.add(Event("reminder3",EventType.REMINDER, LocalDateTime.now().plusDays(12)))
    list.add(Event("reminder3",EventType.EXAM, LocalDateTime.now().plusDays(12)))
    list.add(Event("reminder3",EventType.SOCIETY, LocalDateTime.now().plusDays(12)))
    //TODO: Get events from database
    return list
}

fun importSubjects():MutableList<Subject>{
    val list = mutableListOf<Subject>()
    val events1 = mutableListOf<Event>()
    val events2 = mutableListOf<Event>()
    events1.add(Event("Class1", EventType.CLASS, LocalDateTime.now().minusHours(2), LocalDateTime.now(),location = Location("Class Room")))
    events1.add(Event("Lecture1", EventType.LECTURE, LocalDateTime.now(), LocalDateTime.now().plusHours(1),location = Location("Lecture Hall")))
    events2.add(Event("Class1", EventType.CLASS, LocalDateTime.now().plusHours(12), LocalDateTime.now().plusHours(13),location = Location("Class Room")))
    events2.add(Event("Lecture1", EventType.LECTURE, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5),location = Location("Lecture Hall")))
    list.add(Subject("Subject1","An exampleSubject", events1))
    list.add(Subject("Subject2","Second example Subject", events2))
    fun importEvents():MutableList<Event>{
        val allEvents = mutableListOf<Event>()
        for(subject in list){
            for(event in subject.events) {
                allEvents.add(event)
            }
        }
        return allEvents
    }
    return list
}