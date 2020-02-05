package com.example.studentlifeapp.fragments

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.MainActivity
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
        if(menu.findItem(R.id.action_add)!=null){
            menu.findItem(R.id.action_add).isEnabled = false
        }
        super.onPrepareOptionsMenu(menu)
    }
//Todo fix like everything. opening up the fragment means it doesnt change when you change navigation view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_subject, container, false)
        val setStart = view.findViewById<EditText>(R.id.add_subject_start)
        var startDate:String
        setStart.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                startDate = format.format(selectedDate.time)
                add_subject_start.setText(startDate)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
        val setEnd = view.findViewById<EditText>(R.id.add_subject_end)
        var endDate:String
        setEnd.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                endDate = format.format(selectedDate.time)
                add_subject_end.setText(endDate)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
        view.button_save_subject.setOnClickListener{
            addSubject()
        }
        return view
    }

    private fun addSubject(){
        if (add_subject_name.text.isEmpty() || add_subject_start.text.isEmpty()||add_subject_end.text.isEmpty()){
            Toast.makeText(context,"Please fill in all compulsory fields", Toast.LENGTH_SHORT).show()
        }else{
            val db = DatabaseManager()
            subjectName = add_subject_name.text.toString()
            val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm")
            subjectStart = LocalDateTime.parse("${add_subject_start.text} 00:00", formatter)
            subjectEnd = LocalDateTime.parse("${add_subject_end.text} 23:59", formatter)
            val subjectDetails = add_subject_summary.text.toString()
            val subject = Subject(subjectName,subjectDetails, subjectStart = subjectStart, subjectEnd = subjectEnd)

            var subRef:String?
            val docData = hashMapOf(
                "name" to subject.name,
                "summary" to subject.summary,
                "subject_start" to subject.subjectStart.toTimeStamp(),
                "subject_end" to subject.subjectEnd.toTimeStamp()
            )
            db.getDatabase().collection("subjects").add(docData)
                .addOnSuccessListener {documentReference ->
                    subRef = documentReference.id
                    subject.setId(subRef!!)
                    Log.d(TAG,  "Document written with ID: ${documentReference.id}")

                    callback.onSubjectSaved(subject)
                    optionsMenu.findItem(R.id.action_add).isVisible = true
                }
                .addOnFailureListener{e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }


}
