package com.example.studentlifeapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentlifeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.email_edt_text
import kotlinx.android.synthetic.main.activity_login.pass_edt_text
import kotlinx.android.synthetic.main.activity_signup.*

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var signUpBtn: Button
    private lateinit var loginBtn: Button

    private lateinit var db:FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loginBtn = findViewById(R.id.login_btn)
        signUpBtn = findViewById(R.id.signup_btn)

        //Registers a user when valid sign up information entered
        signUpBtn.setOnClickListener{
            val email: String = email_edt_text.text.toString()
            val password: String = pass_edt_text.text.toString()
            val name: String = name_edt_text.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()

            } else if(password.length<6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()

            }

            else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this
                ) { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()

                        //add user to database
                        userID = auth.currentUser!!.uid
                        val documentReference:DocumentReference = db.collection("users").document(userID)
                        val user = hashMapOf(
                            "name" to name,
                            "email" to email)
                        documentReference.set(user)
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot added with ID: $userID")
                            }
                            .addOnFailureListener {exception ->
                                Log.w("TAG", "Error adding document", exception)
                            }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else {
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)

                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
            
        }

        //Open Login activity on click of button
        loginBtn.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
