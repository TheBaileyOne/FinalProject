package com.example.studentlifeapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.StudyMode
import kotlinx.android.synthetic.main.fragment_study_mode.view.*


class StudyModeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_study_mode, container, false)
        val view: View = inflater.inflate(R.layout.fragment_study_mode, container, false)

        view.studyButton.setOnClickListener{
            Log.d("studyButton","Selected")
            val intent = Intent(context, StudyMode::class.java)
            startActivity(intent)

        }
        return view

    }


}
