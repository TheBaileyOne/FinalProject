package com.example.studentlifeapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.example.studentlifeapp.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 2000L
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        val nextActivity = if(auth.currentUser == null)Login::class.java else MainActivity::class.java
        Handler().postDelayed({
            //start main activity
            startActivity(Intent(this@SplashActivity, nextActivity))
            //finish this activity
            finish()
        },SPLASH_TIME_OUT)

    }
}
