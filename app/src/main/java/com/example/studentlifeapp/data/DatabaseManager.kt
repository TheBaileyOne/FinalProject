package com.example.studentlifeapp.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseManager{
    private val user = FirebaseAuth.getInstance().currentUser!!.uid
    private val db = FirebaseFirestore.getInstance().collection("users").document(user)
//

    fun getDatabase(): DocumentReference{
        return db
    }

    fun addSubReference(ref:String, subRef:String, refCollection: String){
        val data = hashMapOf("ref" to ref)
        db.collection("subjects").document(subRef).collection(refCollection)
            .add(data)
            .addOnSuccessListener {docRef->
                Log.d(TAG,  "Document written with ID: ${docRef.id}")
            }
            .addOnFailureListener{e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


}