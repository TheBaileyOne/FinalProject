package com.example.studentlifeapp.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseManager{
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser!!.uid

    fun addEvent(event:Event){
        db.collection("events").add(event)
            .addOnSuccessListener {documentReference ->
                Log.d(TAG,  "Document written with ID: ${documentReference.id}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun checkDate() {
    }
}