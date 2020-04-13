package com.example.studentlifeapp.fragments


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Assessment
import com.example.studentlifeapp.data.EventType
import kotlinx.android.synthetic.main.fragment_add_assessment.*

class AddAssessmentFragment(private val remainingWeight: Int = 100) : Fragment() {

    internal lateinit var callback: OnAssessmentSavedListener
    interface OnAssessmentSavedListener{
        fun onAssessmentSaved(assessment: Assessment)
    }
    fun setOnAssessmentSavedListener(callback:OnAssessmentSavedListener){
        this.callback = callback
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_assessment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar_assessment_weighting)
        assessment_expand_back.setOnClickListener {
            activity?.onBackPressed()
        }
        seekBar.max = remainingWeight
        add_assessment_max_weight.text = getString(R.string.max_weight_string, remainingWeight)
//        add_assessment_max_weight.text = "/$remainingWeight%"
        var setWeight: Int = 0
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                add_assessment_weighting_text.text = progress.toString()
                setWeight = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG, "seekbar touch started")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG, "seekbar touch stopped")

            }
        })

        button_save_assessment.setOnClickListener {
            if(add_assessment_mark_edit.text.isNullOrBlank() || add_assessment_name_edit.text.isNullOrBlank() || add_assessment_max_mark_edit.text.isNullOrBlank()){
                Toast.makeText(context, "Fill in Empty fields", Toast.LENGTH_SHORT).show()
            }
            else {
                val name = add_assessment_name_edit.text.toString()
                val maxMark = add_assessment_max_mark_edit.text.toString().toDouble()
                val mark = add_assessment_mark_edit.text.toString().toDouble()
                val weight = setWeight.toDouble()
                val assessment = Assessment(name, mark, maxMark, weight, EventType.EXAM)
                callback.onAssessmentSaved(assessment)
                this.activity?.onBackPressed()
            }
        }
    }

}
