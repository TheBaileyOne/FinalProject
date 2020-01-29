package com.example.studentlifeapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.studentlifeapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class Signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var nameEt: EditText

    private lateinit var signUpBtn: Button
    private lateinit var loginBtn: Button

    private lateinit var db:FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        emailEt = findViewById(R.id.email_edt_text)
        passwordEt = findViewById(R.id.pass_edt_text)
        nameEt = findViewById(R.id.name_edt_text)

        loginBtn = findViewById(R.id.login_btn)
        signUpBtn = findViewById(R.id.signup_btn)

        signUpBtn.setOnClickListener{
            var email: String = emailEt.text.toString()
            var password: String = passwordEt.text.toString()
            var name: String = nameEt.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()

            } else if(password.length<6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()

            }

            else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()

                        //add user to database
                        userID = auth.currentUser!!.uid
                        val documentReference:DocumentReference = db.collection("users").document(userID)
                        var user = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "password" to password)
                        documentReference.set(user)
                            .addOnSuccessListener { documentReference ->
                                Log.d("TAG", "DocumentSnapshot added with ID: $userID")
                            }
                            .addOnFailureListener {exception ->
                                Log.w("TAG", "Error adding document", exception)
                            }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        Log.w("TAG", "createUserWithEmail:failure", task.exception);

                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                    }
                })
            }
            
        }

        loginBtn.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
