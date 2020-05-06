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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_NO_TITLE) //No Titlebar for screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN) //Make FullScreen
        setContentView(R.layout.activity_splash)
        //Check current user and open relevant activity. If no user login, if user is logged in main activity
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val intent:Intent = if(currentUser!=null)Intent(this@SplashActivity,MainActivity::class.java)
                            else(Intent(this@SplashActivity, Login::class.java) )
        Handler().postDelayed({
            startActivity(intent)
            finish()
        },splashTimeOut)

    }
}
