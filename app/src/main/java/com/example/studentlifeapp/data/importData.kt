package com.example.studentlifeapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import org.threeten.bp.LocalDateTime
//import org.threeten.bp.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
fun importEvents():List<Event> {
    val list = mutableListOf<Event>()
//    val currentMonth = YearMonth.now()


    list.add(Event("Reminder1", EventType.REMINDER, LocalDateTime.now()))
    list.add(Event("Reminder1.1",EventType.REMINDER, LocalDateTime.now()))
    list.add(Event("Reminder1.2", EventType.REMINDER,LocalDateTime.now()))
    list.add(Event("Event1", EventType.LECTURE, LocalDateTime.now().plusDays(4),location = Location("somePlace")))
    list.add(Event("Reminder2", EventType.REMINDER,LocalDateTime.now().plusHours(12)))
    list.add(Event("reminder3",EventType.REMINDER, LocalDateTime.now().plusDays(12)))
    //TODO: Get events from database
    return list
}