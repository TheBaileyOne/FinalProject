package com.example.studentlifeapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.studentlifeapp.fragments.Event
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
fun importEvents():List<Event> {
    val list = mutableListOf<Event>()
    val currentMonth = YearMonth.now()

    list.add(Event(LocalDateTime.now()))

    //TODO: Get events from database
    return list
}