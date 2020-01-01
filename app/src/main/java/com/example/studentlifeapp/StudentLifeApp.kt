package com.example.studentlifeapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class StudentLifeApp : Application(){
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}