package com.example.studentlifeapp.fragments

import android.content.ContentValues.TAG
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.AcademicYear
import com.example.studentlifeapp.data.Classification
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.util.calculateClassification
import kotlinx.android.synthetic.main.fragment_course_tab.*
import kotlinx.coroutines.selects.select

class CourseTabFragment : Fragment() {
    private lateinit var viewModel: SubjectsViewModel
    private var subjectRows: MutableList<Pair<Subject, TableRow>> = mutableListOf()
    private lateinit var checked: MutableList<Boolean>
    private var requiredCredits: Int = 20

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_course_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tableLayout = course_grade_table
        required_credits.text = getString(R.string.required_credits, 20)
        viewModel = activity?.run{
            ViewModelProviders.of(this).get(SubjectsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        course_grade_obtained.text = getString(R.string.obtained_percent,0.0)
        course_grade_classification.text = getString(R.string.classification, Classification.FAIL)
        viewModel.subjects.observe(viewLifecycleOwner, Observer<MutableList<Subject>>{subjects ->
            clearTable(tableLayout)
            setTable(subjects, tableLayout)
            checked = MutableList(subjectRows.size){false}
            for (i in 0 until subjectRows.size){
//            for (pair in subjectRows){
                val pair = subjectRows[i]
                val row = pair.second
                val subject = pair.first
                val checkBox = row.getChildAt(3) as CheckBox

                checkBox.setOnCheckedChangeListener{buttonView, isChecked ->
                    checked[i] = isChecked
                    updateTotal()
                }

            }

        })

        val max = 360
        val min = 20
        val step = 5
        seekbar_credits.max = (max-min)/step
        seekbar_credits.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val progressCustom = min + (progress*step)
                required_credits.text = getString(R.string.required_credits, progressCustom)
                requiredCredits = progressCustom
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG, "tracking")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG, "stopped tracking")
                updateTotal()
            }

        })

    }

    private fun updateTotal(){
        var totalPercent = 0.0
        var lowestPercent = 100.0
        var classification:String
        var selectedCredits = 0
        for (i in 0 until subjectRows.size){
            if (checked[i]){
                val subPercent = subjectRows[i].first.percentage
                val subCredits = subjectRows[i].first.credits
                selectedCredits += subCredits
                val weighting = subCredits/requiredCredits.toDouble()
                val weightedPercent = subPercent * weighting
                totalPercent += weightedPercent
            }
        }
        classification = if (selectedCredits<= requiredCredits){
                when (calculateClassification(totalPercent)){
                    Classification.LOWER_SECOND -> "2:2"
                    Classification.FAIL -> "Fail"
                    Classification.PASS -> "Pass"
                    Classification.UPPER_SECOND -> "2:1"
                    Classification.FIRST -> "First"
                    Classification.INVALID -> "INVALID"
                }
            }else{
                "INVALID"
            }
        course_grade_obtained.text = getString(R.string.obtained_percent, totalPercent)
        course_grade_classification.text = getString(R.string.classification, classification)

    }

    private fun clearTable(table:TableLayout){
        if (table.childCount>1){
            table.removeViews(1,table.childCount -1)
        }
    }

    private fun setTable(subjects:MutableList<Subject>, table:TableLayout){
        var currentYear:AcademicYear? = null
        for (subject in subjects){
            if(subject.academicYear != currentYear){
                val yearRow = insertYearRow(subject.academicYear)
                table.addView(yearRow)
                currentYear = subject.academicYear

            }
            val row = newRow(subject)
            subjectRows.add(Pair(subject,row))
            table.addView(row)
        }

    }

    private fun insertYearRow(year:AcademicYear):TableRow{
        val row = TableRow(context)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT)
//        layoutParams.gravity = Gravity.CENTER
        row.layoutParams = layoutParams

        val subjectText = TextView(context)
        subjectText.text = year.string
        subjectText.setTypeface(subjectText.typeface, Typeface.BOLD)
        subjectText.setPadding(8,0,0,0)

        row.addView(subjectText)
        row.setBackgroundColor(resources.getColor(R.color.AliceBlue))

        return row


    }

    private fun newRow(subject:Subject):TableRow{
        val row = TableRow(context)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT)
//        layoutParams.gravity = Gravity.CENTER
        row.layoutParams = layoutParams
        val subjectText = TextView(context)
        subjectText.text = subject.name
        subjectText.setPadding(8,0,0,0)
        val creditsText = TextView(context)
        creditsText.text = subject.credits.toString()
        creditsText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        val scoreText = TextView(context)
        scoreText.text = getString(R.string.subject_percent_string, subject.percentage)
        scoreText.textAlignment = View.TEXT_ALIGNMENT_CENTER

        val checkBox = CheckBox(context)
        checkBox.gravity = Gravity.CENTER
        row.addView(subjectText)
        row.addView(creditsText)
        row.addView(scoreText)
        row.addView(checkBox)
        return row
    }


}
