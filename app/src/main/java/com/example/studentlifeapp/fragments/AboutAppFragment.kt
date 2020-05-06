package com.example.studentlifeapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.MainActivity

/**
 * Fragment to display information about app and libraries used for it
 */
class AboutAppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).supportActionBar?.title = "About"
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return inflater.inflate(R.layout.fragment_about_app, container, false)
    }






}
