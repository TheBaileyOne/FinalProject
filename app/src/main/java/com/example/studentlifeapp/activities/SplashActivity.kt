package com.example.studentlifeapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.studentlifeapp.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
//    private val splashTimeOut= 2000L
//    private lateinit var auth: FirebaseAuth
//    private var loggedIn:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null){
            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            Toast.makeText(this,"Welcome Back",Toast.LENGTH_SHORT).show()
        }else{
            startActivity(Intent(this@SplashActivity, Login::class.java))
        }
//        val nextActivity = if(!loggedIn) Login::class.java else MainActivity::class.java
//        Toast.makeText(this, "current user: $currentUser", Toast.LENGTH_SHORT).show()
//        startActivity(Intent(this@SplashActivity, nextActivity))

//        val listener = FirebaseAuth.AuthStateListener {
//            val currentUser = auth.currentUser
//            if (currentUser != null){
//                loggedIn = true
//            }
//            val nextActivity = if(!loggedIn) Login::class.java else MainActivity::class.java
//            Toast.makeText(this, "current user: $currentUser", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this@SplashActivity, nextActivity))
//
//        }
//        auth.addAuthStateListener(listener)
//        auth.removeAuthStateListener(listener)
        finish()



    }
}
