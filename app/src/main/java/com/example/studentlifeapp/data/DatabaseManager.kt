package com.example.studentlifeapp.data

import com.google.firebase.firestore.FirebaseFirestore

class DatabaseManager{
    fun addUser(name:String, email:String){
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )
    }
}