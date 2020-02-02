package com.example.studentlifeapp.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.studentlifeapp.toTimeStamp
import kotlinx.android.parcel.RawValue
import org.threeten.bp.LocalDateTime
import java.io.Serializable

class Subject(val name: String, val summary: String, val events:MutableList<String> = mutableListOf(),val subjectStart: LocalDateTime = LocalDateTime.now(),val subjectEnd:LocalDateTime = subjectStart.plusMonths(1)){
//class Subject(val name: String, val summary: String, val events:MutableList<Event> = mutableListOf(),val subjectStart: LocalDateTime = LocalDateTime.now(),val subjectEnd:LocalDateTime = subjectStart.plusMonths(1)){
//    fun addEvents(newEvents: MutableList<Event>){
//        for (event in newEvents){
//            events.add(event)
//        }
//    }
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
                    db.addSubjectEvent(eventRef, id)
                }
                .addOnFailureListener{e ->
                    Log.w(TAG, "Error adding document", e)
                }

        }
    }


}
