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
import com.example.studentlifeapp.activities.MainActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account_management.*

/**
 * Fragment for changing password
 * Future versions will implement more account management functionality
 */
class AccountManagementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser

        (activity as MainActivity).supportActionBar?.title = "Change Password"
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Change users password method
        save_password_button.setOnClickListener {
            if (old_password_edit.text.isNullOrBlank() || new_password_edit.text.isNullOrBlank()
                || confirm_password_edit.text.isNullOrBlank()){
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }else{
                val oldPassword = old_password_edit.text.toString()
                val credential = EmailAuthProvider.getCredential(user!!.email.toString(), oldPassword)
                user.reauthenticate(credential) //Checks whether user credentials are valid
                    .addOnSuccessListener {
                        val newPassword = new_password_edit.text.toString()
                        val confirmPassword  = confirm_password_edit.text.toString()
                        when {
                            newPassword.length <6 -> {
                                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
                            }
                            newPassword != confirmPassword -> {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener{task ->
                                        if (task.isSuccessful){
                                            Toast.makeText(context, "Password Changed", Toast.LENGTH_SHORT).show()
                                            Log.d(TAG, "User password updated")
                                        }else{
                                            Log.d(TAG, "Error updating password")
                                            Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

}
