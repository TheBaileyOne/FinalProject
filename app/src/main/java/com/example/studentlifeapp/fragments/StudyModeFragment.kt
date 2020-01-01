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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StudyModeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StudyModeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudyModeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
