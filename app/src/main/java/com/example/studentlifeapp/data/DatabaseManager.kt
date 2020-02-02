package com.example.studentlifeapp.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.studentlifeapp.toTimeStamp
import com.example.studentlifeapp.tolocalDateTime
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class DatabaseManager{
    private val user = FirebaseAuth.getInstance().currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()
//    private val db = FirebaseFirestore.getInstance().collection("users").document(user)
//    private val db = FirebaseFirestore.getInstance()
    private lateinit var events: MutableList<Event>

    interface DbEventsCallback{
        fun onCallBack(events:MutableList<Event>)
    }

    fun getDatabase(): DocumentReference{
        return db.collection("users").document(user)
    }

    fun setEvents(events: MutableList<Event>){
        this.events = events
    }
    fun getEvents() = events

    fun addEvent(event:Event):String?{

        var eventRef:String? = null
        val docData = hashMapOf(
            "title" to event.title,
            "type" to event.type,
            "start_time" to event.startTime.toTimeStamp(),
            "end_time" to event.endTime.toTimeStamp(),
            "note" to event.note,
            "eventId" to event.eventId
            //TODO: add notifications and location and times
        )
        db.collection("users").document(user).collection("events").add(docData)
            .addOnSuccessListener {documentReference ->
                eventRef = documentReference.id
                Log.d(TAG,  "Document written with ID: ${documentReference.id}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
        return eventRef
    }

    fun addSubject(subject:Subject):String?{
//        val events = if (subject.events.size<1) null else for(event in subject.events) addSubjectEvent(event!!)
        var subRef:String? = null
        val docData = hashMapOf(
            "name" to subject.name,
            "summary" to subject.summary,
            "subject_start" to subject.subjectStart.toTimeStamp(),
            "subject_end" to subject.subjectEnd.toTimeStamp()
        )
        db.collection("users").document(user).collection("subjects").add(docData)
            .addOnSuccessListener {documentReference ->
                subRef = documentReference.id
                subject.setId(subRef!!)
                Log.d(TAG,  "Document written with ID: ${documentReference.id}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }

        return subRef
    }

    fun addSubjectEvent(ref:String){
        val data = hashMapOf("ref" to ref)
        db.collection("users").document(user).collection("subjects").document(ref).collection("eventRef")
            .add(data)
            .addOnSuccessListener {docRef->
                Log.d(TAG,  "Document written with ID: ${docRef.id}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
    fun addSubjectEvent(ref:String, subRef:String){
        val data = hashMapOf("ref" to ref)
        db.collection("users").document(user).collection("subjects").document(subRef).collection("eventRef")
            .add(data)
            .addOnSuccessListener {docRef->
                Log.d(TAG,  "Document written with ID: ${docRef.id}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


    fun checkDate() {
    }
    fun getEventsByDate(dateTime: LocalDateTime):List<Event>{
        var date = dateTime.toLocalDate()
        return emptyList()

    }

    fun exportEvents(events: MutableList<Event>){
        for (event in events){
            addEvent(event)
        }
    }

    fun importEvents(dbEventsCallback: DbEventsCallback){
        val events = mutableListOf<Event>()
        db.collection("events").get()
            .addOnSuccessListener{result ->
                for (document in result){
                    if (document !=null) {
                        Log.d(TAG, "${document.id}=> ${document.data}")

                        // val event = document.toObject(Event::class.java)
                        val event = Event(
                            title = document.getString("title")!!,
                            startTime = (document.get("start_time") as Timestamp).tolocalDateTime(),
                            endTime = (document.get("end_time") as Timestamp).tolocalDateTime(),
                            note = document.getString("note"),
                            eventId = document.getString("eventId")!!
                        )
                        events.add(event)
                        Log.d("Add event", "event: $event added")
                    }
                }
                dbEventsCallback.onCallBack(events)
//                this.events = events
            }
            .addOnFailureListener{e->
                Log.d(TAG,"Error getting documents: ", e)
            }
        Log.d("event size", "size = ${events.size}")
    }



}