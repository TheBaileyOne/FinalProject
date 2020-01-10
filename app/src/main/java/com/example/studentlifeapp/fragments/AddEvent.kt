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
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.Location
import kotlinx.android.synthetic.main.fragment_add_event.*
import kotlinx.android.synthetic.main.fragment_add_event.view.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalUnit
import java.text.SimpleDateFormat
import java.util.*

class AddEvent(private val subjectEnd: LocalDateTime? = null) : Fragment() {



    internal lateinit var callback: OnEventSavedListener

    interface OnEventSavedListener{
        fun onEventSaved(events:MutableList<Event>)
    }
    fun setOnEventSavedListener(callback: OnEventSavedListener){
        this.callback = callback
    }
    //TODO: Notifications
    private val format = SimpleDateFormat("dd MMM, YYYY", Locale.UK)
    private val timeFormat = SimpleDateFormat ("HH:mm", Locale.UK)
    lateinit var eventName:String
    lateinit var eventStartTime: LocalDateTime
    lateinit var eventEndTime:LocalDateTime
    lateinit var location :Location
    lateinit var notes:String
    lateinit var eventType: EventType
    lateinit var durationValue: String
    lateinit var eventId: String
//    val repeatTimes:MutableList = mutableListOf<LocalDateTime>()
//    lateinit var event:Event
    val events:MutableList<Event> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_event, container, false)
        val nameText = view.findViewById<EditText>(R.id.add_event_name)
        nameText.hint = "e.g. Geography101: lecture"
        //Event Type Spinner
        val spinner = view.findViewById<Spinner>(R.id.event_type_spinner)
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
//                val type = parent?.getItemAtPosition(position)
//                Toast.makeText(context,"Type: $type",Toast.LENGTH_SHORT).show()
                var type = parent?.getItemAtPosition(position)
                type = type.toString()
                eventType = EventType.valueOf(type)
                Toast.makeText(context,"Type: $type",Toast.LENGTH_SHORT).show()
            }
        }

        //Start Date Selection
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

        //Event end date pickers
        val setFinishDate = view.findViewById<EditText>(R.id.add_event_end_date)
        val setFinishTime = view.findViewById<EditText>(R.id.add_event_time_end)
        var finishDateDate:String
        var finishDateTime:String

        setFinishDate.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker2 = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                Toast.makeText(context,"Date picker picked", Toast.LENGTH_SHORT)
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
        val spinnerRep = view.findViewById<Spinner>(R.id.spinner_repeat)
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
                Toast.makeText(context,"Time: $type",Toast.LENGTH_SHORT).show()
                durationValue = type.toString()
                if (durationValue.equals("Never")){
                    add_event_repeat_num.visibility = View.GONE
                }else{
                    add_event_repeat_num.visibility = View.VISIBLE
                }
            }
        }
        view.button_add_event.setOnClickListener{
            addEvent()
//            Toast.makeText(context,"Event Saved", Toast.LENGTH_SHORT)

        }

        return view
    }

    fun dateDialogSet(editText:EditText?){
        TODO("Put reusable code in")
    }

    fun timeDialogSet(editText:EditText?){
        TODO("Put reusable code in")
    }

    private fun addEvent(){
        if(add_event_name.text.isEmpty()|| add_event_date.text.isEmpty() ||
            add_event_time.text.isEmpty() || add_event_end_date.text.isEmpty() || add_event_time_end.text.isEmpty() ||
            (add_event_repeat_num.text.isEmpty() && durationValue != "never")){
            Toast.makeText(context,"Please fill in all compulsory fields",Toast.LENGTH_SHORT).show()
        }else{

            val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm")
            eventName = add_event_name.text.toString()
            eventStartTime = LocalDateTime.parse("${add_event_date.text} ${add_event_time.text}",formatter)
            eventEndTime = LocalDateTime.parse("${add_event_end_date.text} ${add_event_time_end.text}",formatter)
            notes = add_event_notes.text.toString()
            eventId = "${eventType.name}: ${eventName}"
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
//                    do {
//                        newStart = when(durationValue){
//                            "Days" -> events[count].startTime.plusDays(durationNumber)
//                            "Weeks" -> events[count].startTime.plusWeeks(durationNumber)
//                            "Months" -> events[count].startTime.plusMonths(durationNumber)
//                            "Years" -> events[count].startTime.plusYears(durationNumber)
//                            else -> throw Exception("Invalid repeat variable")
//                        }
//                        newEnd = newStart.plusHours(eventLength)
//                        events.add(events[count].copy(startTime = newStart, endTime = newEnd))
//                        count++
//                    }while(newStart.isBefore(subjectEnd))
                }
            }
            callback.onEventSaved(events)
            this.activity?.onBackPressed()
        }
    }




    private fun repeatEvent():LocalDateTime{
        TODO("repeat event creation a certain amount of times, or until a certain date")
        val now = Calendar.getInstance()

        val lastRepeat = Calendar.getInstance()
        val datePicker = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            lastRepeat.set(Calendar.YEAR,year)
            lastRepeat.set(Calendar.MONTH,month)
            lastRepeat.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
        datePicker.show()

        return LocalDateTime.now()
    }

}
