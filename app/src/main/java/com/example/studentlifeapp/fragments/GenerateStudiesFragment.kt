package com.example.studentlifeapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.util.StudyGenerator
import kotlinx.android.synthetic.main.fragment_generate_studies.*
import kotlinx.android.synthetic.main.fragment_generate_studies.view.*
import kotlinx.android.synthetic.main.fragment_studies_generated.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match


class GenerateStudiesFragment(val events:MutableList<Event>, val subject: Subject?) : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_studies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studyName = view.findViewById<EditText>(R.id.add_study_name_edit)
        val endDate = view.findViewById<EditText>(R.id.add_study_end_date_edit)
        val radioGroup = view.findViewById<RadioGroup>(R.id.study_lunch_radioGroup)
        val weekendStudy = view.findViewById<Switch>(R.id.weekend_study_switch)
        var endDateString:String = ""
        val format = SimpleDateFormat("dd MMM, YYYY", Locale.UK)
        var time = LocalTime.of(13,0)
        add_study_expand_back.setOnClickListener {
            activity?.onBackPressed()
        }
        endDate.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(context!!,DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                endDateString = format.format(selectedDate.time)
                add_study_end_date_edit.setText(endDateString)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
        radioGroup.setOnCheckedChangeListener{ _, checkedId->
            when(checkedId){
                R.id.study_lunch12 -> time = LocalTime.of(12,0)
                R.id.study_lunch13 -> time = LocalTime.of(13,0)
            }
        }

        view.button_save_study.setOnClickListener{
            if (studyName.text.isNullOrEmpty()|| endDate.text.isNullOrBlank()){
                Toast.makeText(context, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else{
                val name = studyName.text.toString()
                val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
                val date = LocalDate.parse(endDateString, formatter)
                val weekendBool = weekendStudy.isChecked
                generateStudy(name,date,time, weekendBool)
            }
        }
    }

    private fun generateStudy(name:String, endDate:LocalDate, lunchTime:LocalTime, weekendStudy:Boolean){
        val studyGenerator = StudyGenerator(name,endDate = endDate, events = events, lunchTime = lunchTime, weekendStudy = weekendStudy)
        val studies = studyGenerator.getStudies()
        val fragment = StudiesGeneratedFragment(studies)
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.subject_detail_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
