package com.example.studentlifeapp.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.Login
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_forgotten_password.*

/**
 * Fragment for sending password reset link to email address
 */
class ForgottenPassword : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgotten_password, container, false)
        view.bringToFront()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        send_reset_button.setOnClickListener{
            if(send_email_edit.text.isNullOrBlank()){
                Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
            }else{
                val email = send_email_edit.text.toString()
                FirebaseAuth.getInstance().sendPasswordResetEmail(email) //Sends email to stated email address to change it
                    .addOnCompleteListener{
                        activity?.onBackPressed()
                        Toast.makeText(context, "Email sent", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e->
                        Log.d(TAG, "$e")
                    }
            }
        }

    }

}
