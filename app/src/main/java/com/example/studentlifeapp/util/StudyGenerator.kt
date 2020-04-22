package com.example.studentlifeapp.util

import android.util.Log
import android.view.View
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.toTimeStamp
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import kotlin.math.ceil


class StudyGenerator(private val name:String, private val startDate: LocalDateTime = LocalDateTime.now(),
                     private val endDate: LocalDate, val events:List<Event>,
                     private var studyLength:Long = 1L, private val dayInterval:Long = 1L,
                     private val lunchTime: LocalTime = LocalTime.of(13,0),
                     private val timeBeg:LocalTime = LocalTime.of(9,0),
                     private val timeEnd:LocalTime = LocalTime.of(19,0),
                     private val weekendStudy:Boolean = true,
                     private val weeklyStudy:Long = 14L
){

    private val studyList = mutableListOf<Event>()
    private val dailyStudy = if(weekendStudy) weeklyStudy/7 else ceil(weeklyStudy/5.0).toLong()
    private val groupedEvents = events.groupBy{it.startTime.toLocalDate()}
//    private val studyLength = 1L

    fun getStudies():MutableList<Event>{
        Log.d("Study Generator", "generating studies")

        generateStudy()
        return studyList
    }

    private fun generateStudy(){
        var day = LocalDate.now()
        while (day<=endDate){
            var dayTime = dailyStudy //amount of time spent studying on that day
            var time = timeBeg
//            var freeTime = false
            if(!weekendStudy && day.dayOfWeek == DayOfWeek.SATURDAY ){
                day = day.plusDays(2)
            }
            else if (!weekendStudy && day.dayOfWeek == DayOfWeek.SUNDAY ){
                day = day.plusDays(1)
            }
            else {
                if (checkDayEvents(day)) {
                    val todayEvents =
                        (groupedEvents[day] ?: error("Should not be null")).toMutableList()
                    do {
                        Log.d("Today Events", "$day: ${todayEvents.size}")
                        val firstOverlap = checkOverlapEvents(todayEvents, time)
                        when {
                            firstOverlap != null -> {
                                time = firstOverlap.endTime.toLocalTime()
                                todayEvents.remove(firstOverlap)
                            }
                            checkOverlap(time, lunchTime, lunchTime.plusHours(1)) -> {
                                time = lunchTime.plusHours(1)
                            }
                            else -> {
                                val eventLength = if (dayTime == 1L || checkOverlapEvents(
                                        todayEvents,
                                        time.plusHours(1)
                                    ) != null
                                ) 1L
                                else if (!weekendStudy && checkOverlapEvents(
                                        todayEvents,
                                        time.plusHours(3)
                                    ) == null
                                ) 3L
                                else 2L
                                studyList.add(
                                    Event(
                                        "$name: Study",
                                        EventType.STUDY,
                                        LocalDateTime.of(day, time),
                                        LocalDateTime.of(day, time.plusHours(eventLength))
                                    )
                                )
                                dayTime -= eventLength
                            }
                        }
                    } while ((time.plusHours(1).isBefore(timeEnd) && dayTime > 0))
                    //                }while ((time.plusHours(1).isBefore(timeEnd) && !freeTime))
                } else {
                    studyList.add(
                        Event(
                            "$name: Study",
                            EventType.STUDY,
                            LocalDateTime.of(day, timeBeg),
                            LocalDateTime.of(day, timeBeg.plusHours(dailyStudy))
                        )
                    )
                }
                day = day.plusDays(dayInterval)
            }

        }
        Log.d("Study Generator", "generated study events: $studyList")
    }

    private fun checkOverlap(checkTime:LocalTime, startTime:LocalTime, endTime:LocalTime):Boolean {
        return (checkTime.isBefore(endTime) && checkTime.isAfter(startTime)) || checkTime == startTime ||
                (checkTime.plusHours(studyLength).isBefore(endTime) && checkTime.plusHours(studyLength).isAfter(startTime))
    }

    private fun checkOverlapEvents(events: MutableList<Event>, checkTime:LocalTime) = events.find{checkOverlap(checkTime,it.startTime.toLocalTime(),it.endTime.toLocalTime())}

    //function to check if events occur on that day
    private fun checkDayEvents(day: LocalDate)= groupedEvents.containsKey(day)

}