package com.example.studentlifeapp.fragments

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.data.AcademicYear
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.toTimeStamp
import kotlinx.android.synthetic.main.fragment_add_subject.*
import kotlinx.android.synthetic.main.fragment_add_subject.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class AddSubjectFragment : Fragment() {

    internal lateinit var callback: OnSubjectSavedListener

    interface OnSubjectSavedListener{
        fun onSubjectSaved(subject:Subject)
    }

    fun setOnSubjectSavedListener(callback: OnSubjectSavedListener){
        this.callback = callback
    }

    private val format = SimpleDateFormat("dd MMM, YYYY", Locale.UK)
    lateinit var subjectName: String
    lateinit var subjectStart: LocalDateTime
    lateinit var subjectEnd: LocalDateTime
    lateinit var optionsMenu:Menu
    private var academicYear:AcademicYear = AcademicYear.FIRST_YEAR

    //TODO: make it so that main activity opens this fragment instead so listener can be used

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).showBottomNav(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        optionsMenu = menu
        menu.findItem(R.id.option_about_app).isVisible = false
        menu.findItem(R.id.option_logout).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
//Todo fix like everything. opening up the fragment means it doesnt change when you change navigation view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_subject, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_subject_background.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            activity?.onBackPressed()
        }

        val setStart = view.findViewById<EditText>(R.id.add_subject_start_edit)
        var startDate:String

        setStart.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                startDate = format.format(selectedDate.time)
                add_subject_start_edit.setText(startDate)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
        val setEnd = view.findViewById<EditText>(R.id.add_subject_end_edit)
        var endDate:String
        setEnd.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                endDate = format.format(selectedDate.time)
                add_subject_end_edit.setText(endDate)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
        val spinner = view.findViewById<Spinner>(R.id.academic_year_spinner)
        val values = enumValues<AcademicYear>().map { it.string }
        spinner?.adapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, values).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner?.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Something to do with an error")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val type = parent?.getItemAtPosition(position)
//                Toast.makeText(context,"Type: $type",Toast.LENGTH_SHORT).show()
                val year = parent?.getItemAtPosition(position) as String
//                academicYear = year as AcademicYear
                academicYear = AcademicYear.values().first { it.string == year }
                Toast.makeText(context,"Type: $year",Toast.LENGTH_SHORT).show()
            }
        }
        view.button_save_subject.setOnClickListener{
            addSubject()
        }
    }

    private fun addSubject(){
        if (add_subject_name_edit.text.isNullOrBlank() || add_subject_start_edit.text.isNullOrBlank()||add_subject_end_edit.text.isNullOrBlank()){
            Toast.makeText(context,"Please fill in all compulsory fields", Toast.LENGTH_SHORT).show()
        }else{
            val db = DatabaseManager()
            subjectName = add_subject_name_edit.text.toString()
            val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm")
            subjectStart = LocalDateTime.parse("${add_subject_start_edit.text} 00:00", formatter)
            subjectEnd = LocalDateTime.parse("${add_subject_end_edit.text} 23:59", formatter)
            val subjectDetails = add_subject_summary.text.toString()
            val subject = Subject(subjectName,subjectDetails, subjectStart = subjectStart, subjectEnd = subjectEnd)
            val credits = if (add_subject_credits_edit.text.isNullOrBlank()) 20 else add_subject_credits_edit.text.toString().toInt()
            subject.credits = credits
            subject.academicYear = academicYear


            var subRef:String?
            val docData = hashMapOf(
                "name" to subject.name,
                "summary" to subject.summary,
                "subject_start" to subject.subjectStart.toTimeStamp(),
                "subject_end" to subject.subjectEnd.toTimeStamp(),
                "credits" to  subject.credits,
                "academic_year" to subject.academicYear
            )
            db.getDatabase().collection("subjects").add(docData)
                .addOnSuccessListener {documentReference ->
                    subRef = documentReference.id
                    subject.setId(subRef!!)
                    Log.d(TAG,  "Document written with ID: ${documentReference.id}")

                    callback.onSubjectSaved(subject)
                }
                .addOnFailureListener{e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }


}
