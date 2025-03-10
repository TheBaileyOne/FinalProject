package com.example.studentlifeapp.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.studentlifeapp.toTimeStamp
import com.example.studentlifeapp.util.calculateClassification
import org.threeten.bp.LocalDateTime

class Subject(val name: String, val summary: String, val events:MutableList<String> = mutableListOf(),
              val subjectStart: LocalDateTime = LocalDateTime.now(), val subjectEnd:LocalDateTime = subjectStart.plusMonths(1),
              var credits:Int = 20, val assessments: MutableList<String> = mutableListOf(), var percentage:Double = 0.0,
              var remainingWeight:Int = 100, var academicYear: AcademicYear = AcademicYear.FIRST_YEAR){
    private var id: String = ""
    fun setId(id:String){
        this.id = id
    }

    fun getId() = id

    //Add subjects events to datbaseas, and add their references to subject sub collection
    fun addEvents(newEvents: MutableList<Event>){
        Log.d("Subject.addEvents" ,"events: $newEvents")
        val db = DatabaseManager()
        for (event in newEvents){
            var eventRef: String
            val docData = hashMapOf(
                "title" to event.title,
                "type" to event.type,
                "start_time" to event.startTime.toTimeStamp(),
                "end_time" to event.endTime.toTimeStamp(),
                "note" to event.note,
                "eventId" to event.eventId
            )
            db.getDatabase().collection("events").add(docData)
                .addOnSuccessListener {documentReference ->
                    eventRef = documentReference.id
                    Log.d(TAG,  "Document written with ID: ${documentReference.id}")
                    events.add(eventRef)
                    db.addSubReference(eventRef, id, "eventRef")
//                    db.addSubjectEvent(eventRef, id)
                }
                .addOnFailureListener{e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
    fun addAssessment(assessment: Assessment){
        Log.d("Subject.addAssessments" ,"events: $assessment")
        val db = DatabaseManager()
        var assessmentRef: String
        val docData = hashMapOf(
            "name" to assessment.name,
            "mark" to assessment.mark,
            "maxMark" to assessment.maxMark,
            "weighting" to assessment.weighting,
            "type" to assessment.type
        )
        db.getDatabase().collection("assessments").add(docData)
            .addOnSuccessListener {documentReference ->
                assessmentRef = documentReference.id
                Log.d(TAG,  "Document written with ID: ${documentReference.id}")
                assessments.add(assessmentRef)
                db.addSubReference(assessmentRef, id, "assessmentRef")
//                db.addSubjectAssessment(assessmentRef, id)
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}

enum class Classification(val string: String){
    FIRST("First"), UPPER_SECOND("2:1"), LOWER_SECOND("2:2"), PASS("Pass"), FAIL("Fail"), INVALID("Invalid")
}

enum class AcademicYear(val string: String){
    FIRST_YEAR("1st Year"),SECOND_YEAR("2nd Year"),THIRD_YEAR("3rd Year"),FOURTH_YEAR("4th Year"),FIFTH_YEAR("5th Year")
}

data class Assessment(
    val name: String,
    var mark: Double = 0.0,
    val maxMark: Double = 100.0,
    val weighting: Double,
    val type: EventType,
    var dbRef: String = "",
    var linkedEvent:String = ""
    //sort out for database implementation.
){
    private var percentage = calculatePercentage()
    fun calculatePercentage():Double {
        return (mark/maxMark)*100
    }
    fun getWeightedPercentage() = percentage*(weighting/100)

//    fun updateMark(mark: Double){
//        this.mark += mark
//        percentage = calculatePercentage()
//        classification = calculateClassification()
////        this.mark = mark
//    }



}