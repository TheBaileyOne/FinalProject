package com.example.studentlifeapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.activities.SubjectDetails
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.toTimeStamp
import kotlinx.android.synthetic.main.fragment_add_event.*
import kotlinx.android.synthetic.main.fragment_add_event.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment to save event details
 */
class AddEventFragment(private val subjectEnd: LocalDateTime? = null, private val editEvent:Event? = null) : Fragment() {

    internal lateinit var callback: OnEventSavedListener

    interface OnEventSavedListener{
        fun onEventSaved(events:MutableList<Event>)
    }
    fun setOnEventSavedListener(callback: OnEventSavedListener){
        this.callback = callback
    }
    private val format = SimpleDateFormat("dd MMM, YYYY", Locale.UK)
    private val timeFormat = SimpleDateFormat ("HH:mm", Locale.UK)
    private val dateFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM, YYYY")
    private val timeFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    lateinit var eventName:String
    lateinit var eventStartTime: LocalDateTime
    lateinit var eventEndTime:LocalDateTime
    lateinit var notes:String
    lateinit var eventType: EventType
    lateinit var durationValue: String
    lateinit var eventId: String
    private var editing: Boolean = false
//    val repeatTimes:MutableList = mutableListOf<LocalDateTime>()
//    lateinit var event:Event
    val events:MutableList<Event> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (activity is MainActivity){
            (activity as MainActivity).supportActionBar?.title = "Edit Event"
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        return inflater.inflate(R.layout.fragment_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinner = view.findViewById<Spinner>(R.id.event_type_spinner_options)
        val values = enumValues<EventType>()
        spinner?.adapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, values).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner?.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Something to do with an error")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var type = parent?.getItemAtPosition(position)
                type = type.toString()
                eventType = EventType.valueOf(type)
            }
        }

        //Start Date Selection
        val setDate = view.findViewById<EditText>(R.id.add_event_date)
        val setTime = view.findViewById<EditText>(R.id.add_event_time)
        var startDateDate:String
        var startDateTime:String
        setDate.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
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

        //Event end date pickers
        val setFinishDate = view.findViewById<EditText>(R.id.add_event_end_date)
        val setFinishTime = view.findViewById<EditText>(R.id.add_event_time_end)
        var finishDateDate:String
        var finishDateTime:String

        setFinishDate.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker2 = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                finishDateDate = format.format(selectedDate.time)
                add_event_end_date.setText(finishDateDate)
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker2.show()
        }

        setFinishTime.setOnClickListener {
            val now = Calendar.getInstance()
            val timePicker2 = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener{view,hourOfDay,minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                selectedTime.set(Calendar.MINUTE,minute)
                finishDateTime = timeFormat.format(selectedTime.time)
                add_event_time_end.setText(finishDateTime)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),true)
            timePicker2.show()
        }

        //Repeat Event a number of times
        val spinnerRep = view.findViewById<Spinner>(R.id.spinner_repeat_options)
        val repeatVal = arrayOf("Never","Days","Weeks","Months","Years")
        spinnerRep?.adapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.simple_spinner_item, repeatVal).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRep.adapter = adapter
        }
        spinnerRep?.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Something to do with an error")
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position)
                durationValue = type.toString()
                if (durationValue.equals("Never")){
                    add_event_repeat_num.visibility = View.GONE
                }else{
                    add_event_repeat_num.visibility = View.VISIBLE
                }
            }
        }

        if(editEvent!=null){
            textView2.text = "Edit Event"
            add_event_name_edit.setText(editEvent.title)
            add_event_date.setText(dateFormatter.format(editEvent.startTime.toLocalDate()))
            add_event_time.setText(timeFormatter.format(editEvent.startTime.toLocalTime()))
            add_event_end_date.setText(dateFormatter.format(editEvent.endTime.toLocalDate()))
            add_event_time_end.setText(timeFormatter.format(editEvent.endTime.toLocalTime()))
            textView7.visibility = View.GONE
            add_event_repeat_num.visibility = View.GONE
            spinner_repeat.visibility = View.GONE
            add_event_notes.setText(editEvent.note)
            spinner.setSelection(values.indexOf(editEvent.type))
            editing = true
        }
        //Add or update event depending on context of fragment
        view.button_add_event.setOnClickListener{
            if (editing&&editEvent!=null) updateEvent() else addEvent()
        }
    }

    /**
     * Update event details in database
     */
    private fun updateEvent() {
        if(add_event_name_edit.text.isNullOrBlank()|| add_event_date.text.isEmpty() ||
            add_event_time.text.isEmpty() || add_event_end_date.text.isEmpty() || add_event_time_end.text.isEmpty()){
            Toast.makeText(context,"Please fill in all compulsory fields",Toast.LENGTH_SHORT).show()
        }else{
            val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm")
            editEvent!!.title = add_event_name_edit.text.toString()
            editEvent.startTime = LocalDateTime.parse("${add_event_date.text} ${add_event_time.text}",formatter)
            editEvent.endTime = LocalDateTime.parse("${add_event_end_date.text} ${add_event_time_end.text}",formatter)
            editEvent.note = add_event_notes.text.toString()
            editEvent.eventId = "${editEvent.type.name}: ${editEvent.title}"


            val data = mapOf(
                "title" to editEvent.title,
                "type" to editEvent.type,
                "start_time" to editEvent.startTime.toTimeStamp(),
                "end_time" to editEvent.endTime.toTimeStamp(),
                "note" to editEvent.note,
                "eventId" to editEvent.eventId)
            var string = ""
            DatabaseManager().getDatabase().collection("events").document(editEvent.eventRef)
                .update(data).addOnSuccessListener {
                    string = "${editEvent.title} updated"
                }.addOnFailureListener{
                    string = "Save error"
                }
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
            if (activity is SubjectDetails){
                (activity as SubjectDetails).updateSubEvents()
            }
            requireActivity().onBackPressed()
        }
    }

    private fun addEvent(){
        if(add_event_name_edit.text.isNullOrBlank()|| add_event_date.text.isEmpty() ||
            add_event_time.text.isEmpty() || add_event_end_date.text.isEmpty() || add_event_time_end.text.isEmpty() ||
            (add_event_repeat_num.text.isEmpty() && durationValue != "Never")){
            Toast.makeText(context,"Please fill in all compulsory fields",Toast.LENGTH_SHORT).show()
        }else{

            val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm")
            eventName = add_event_name_edit.text.toString()
            eventStartTime = LocalDateTime.parse("${add_event_date.text} ${add_event_time.text}",formatter)
            eventEndTime = LocalDateTime.parse("${add_event_end_date.text} ${add_event_time_end.text}",formatter)
            notes = add_event_notes.text.toString()
            eventId = "${eventType.name}: $eventName"
            var event = Event(eventName,eventType,eventStartTime,eventEndTime,note=notes, eventId=eventId)
            events.add(event)


            var newStart:LocalDateTime
            var newEnd:LocalDateTime

            if (durationValue != "Never") {
                val durationNumber = add_event_repeat_num.text.toString().toLong()
                var count = 0
                var eventLength = ChronoUnit.HOURS.between(event.startTime,event.endTime)
                if(subjectEnd != null){
                    do {
                        newStart = when(durationValue){
                            "Days" -> events[count].startTime.plusDays(durationNumber)
                            "Weeks" -> events[count].startTime.plusWeeks(durationNumber)
                            "Months" -> events[count].startTime.plusMonths(durationNumber)
                            "Years" -> events[count].startTime.plusYears(durationNumber)
                            else -> throw Exception("Invalid repeat variable")
                        }
                        newEnd = newStart.plusHours(eventLength)
                        events.add(events[count].copy(startTime = newStart, endTime = newEnd))
                        count++
                        if (events[count].startTime.isAfter(subjectEnd)){
                            events.removeAt(count)
                        }
                    }while(newStart.isBefore(subjectEnd))
                }
            }
            callback.onEventSaved(events)
            if (activity is MainActivity){
                (activity as MainActivity).showBottomNav(true)
            }
            this.activity?.onBackPressed()
        }
    }

}
