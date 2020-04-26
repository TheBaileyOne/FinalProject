package com.example.studentlifeapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.studentlifeapp.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private val splashTimeOut= 500L
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
        var intent:Intent = if(currentUser!=null)Intent(this@SplashActivity,MainActivity::class.java)
                            else(Intent(this@SplashActivity, Login::class.java) )
        Handler().postDelayed({
            startActivity(intent)
            finish()
        },splashTimeOut)

    }
}
