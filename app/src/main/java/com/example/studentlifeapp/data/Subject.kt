package com.example.studentlifeapp.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.studentlifeapp.toTimeStamp
import kotlinx.android.parcel.RawValue
import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.lang.Exception

class Subject(val name: String, val summary: String, val events:MutableList<String> = mutableListOf(),
              val subjectStart: LocalDateTime = LocalDateTime.now(),val subjectEnd:LocalDateTime = subjectStart.plusMonths(1),
              val credits:Int = 20, val assements: MutableList<String> = mutableListOf(), var percentage:Double = 0.0, var remainingWeight:Int = 100){
    private var id: String = ""
    fun setId(id:String){
        this.id = id
    }

    fun getId() = id
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
                //TODO: add notifications and location and times
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
                assements.add(assessmentRef)
                db.addSubReference(assessmentRef, id, "assessmentRef")
//                db.addSubjectAssessment(assessmentRef, id)
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}

enum class Classification{
    FIRST, UPPER_SECOND, LOWER_SECOND, PASS, FAIL
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
    var classification:Classification = calculateClassification()
    val subAssesments = mutableListOf<Assessment>()
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

    fun calculateClassification():Classification{
        return if (percentage in 70.0..100.0){
            Classification.FIRST
        }
        else if (percentage>=60){
            Classification.UPPER_SECOND
        }
        else if (percentage>=50){
            Classification.LOWER_SECOND
        }
        else if (percentage>=40){
            Classification.PASS
        }
        else if (percentage<40 && percentage>=0){
            Classification.FAIL
        }
        else{
            throw Exception("Invalid percentage")
        }
    }

}