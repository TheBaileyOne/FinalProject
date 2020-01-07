package com.example.studentlifeapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.EventType
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_add_event.*
import kotlinx.android.synthetic.main.fragment_add_event.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddEvent : Fragment() {

    val format = SimpleDateFormat("dd MMM, YYYY", Locale.UK)
    var timeFormat = SimpleDateFormat ("HH:mm", Locale.UK)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_event, container, false)
        val spinner = view.findViewById<Spinner>(R.id.event_type_spinner)
        val values = enumValues<EventType>()
        spinner?.adapter = ArrayAdapter(activity?.applicationContext!!, R.layout.support_simple_spinner_dropdown_item, values)
        spinner?.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Something to do with an error")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position)
                Toast.makeText(context,"Type: $type",Toast.LENGTH_SHORT).show()

            }
        }

        val setDate = view.findViewById<EditText>(R.id.add_event_date)
        val setTime = view.findViewById<EditText>(R.id.add_event_time)
        var startDateDate:String
        var startDateTime:String
        setDate.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                startDateDate = format.format(selectedDate.time)
                add_event_date.setText(startDateDate)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        setTime.setOnClickListener {
            val now = Calendar.getInstance()
            val timePicker = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener{view,hourOfDay,minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                selectedTime.set(Calendar.MINUTE,minute)
                startDateTime = timeFormat.format(selectedTime.time)
                add_event_time.setText(startDateTime)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true)
            timePicker.show()
        }

        return view
    }

    fun getString(value:String): String = value

}
