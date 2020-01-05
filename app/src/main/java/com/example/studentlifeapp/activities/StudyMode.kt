package com.example.studentlifeapp.activities

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.studentlifeapp.R

import kotlinx.android.synthetic.main.activity_study_mode.*

class StudyMode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_mode)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "     Study Mode"
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Adds items to the action bar if preseent
        menuInflater.inflate(R.menu.menu_study_mode, menu)
        return true
    }

}
