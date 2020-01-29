package com.example.studentlifeapp.util

import com.example.studentlifeapp.data.Subject
import org.threeten.bp.LocalDateTime

class StudyGenerator(val subject: Subject, val startDate: LocalDateTime = subject.subjectStart, val endDate: LocalDateTime = subject.subjectEnd){
    private val busyTimes = mutableListOf<Pair<LocalDateTime,LocalDateTime>>()
    fun getBusyTimes(){

    }



}