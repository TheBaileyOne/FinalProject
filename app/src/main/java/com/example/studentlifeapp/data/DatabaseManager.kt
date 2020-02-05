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
    private val db = FirebaseFirestore.getInstance().collection("users").document(user)
//

    fun getDatabase(): DocumentReference{
        return db
    }


    fun addSubjectEvent(ref:String, subRef:String){
        val data = hashMapOf("ref" to ref)
        db.collection("subjects").document(subRef).collection("eventRef")
            .add(data)
            .addOnSuccessListener {docRef->
                Log.d(TAG,  "Document written with ID: ${docRef.id}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }



}