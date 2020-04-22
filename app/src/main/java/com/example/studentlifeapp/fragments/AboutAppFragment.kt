package com.example.studentlifeapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import com.example.studentlifeapp.R


class AboutAppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about_app, container, false)
    }



}
