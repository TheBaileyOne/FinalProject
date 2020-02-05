package com.example.studentlifeapp.util

import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.data.importEvents
import com.example.studentlifeapp.toTimeStamp
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class StudyGenerator(val subject: Subject, val startDate: LocalDateTime = subject.subjectStart, val endDate: LocalDateTime = subject.subjectEnd){
//    private val busyTimes = mutableListOf<Pair<LocalDateTime,LocalDateTime>>()
    private val events = importEvents() //TODO: Database query for events between start and end date
    private val studyList = mutableListOf<Event>()

    private fun startGenerator(){

    }

    private fun getStudy(){
        var day = startDate.toLocalDate()
        var freeTime = false
        val timeBeg:LocalTime = LocalTime.of(9,0)
        val timeEnd:LocalTime = LocalTime.of(19,0)
        var time:LocalDateTime = LocalDateTime.of(day, timeBeg)
        while (day<=endDate.toLocalDate()){
            do{
                for(event in events){
//                    if (checkRange(event, time)){
//                        if(checkRange(event,time.plusHours(1))){
//                            studyList.add(Event("Study: ${subject.name}",EventType.STUDY,time,time.plusHours(1)))
//                            freeTime = true
//                        }
//
//                    }


                }
            }while (time.plusHours(1).toLocalTime().isBefore(timeEnd) || !freeTime)


        }
    }

    private fun queryTimes(startLDT: LocalDateTime,endLDT:LocalDateTime ){
        val startTime = startLDT.toTimeStamp()
        val endTime = endLDT.toTimeStamp()

        
    }

    fun checkRange(event: Event, studyTime:LocalDateTime):Boolean{
//        return !(studyTime.isAfter(event.startTime)&& studyTime.isBefore(event.endTime))
        return studyTime.isAfter(event.startTime)&& studyTime.isBefore(event.endTime)
    }



}