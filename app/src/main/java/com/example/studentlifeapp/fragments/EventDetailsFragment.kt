package com.example.studentlifeapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.EventLog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.activities.SubjectDetails
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Event
import kotlinx.android.synthetic.main.fragment_event_details.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ClassCastException

class EventDetailsFragment(val event: Event) : Fragment() {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter2= DateTimeFormatter.ofPattern("EEE, dd MMM")
    private lateinit var eventEditListener: EventEditListener

    interface EventEditListener{
        fun eventEditClicked(event:Event)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventEditListener){
            eventEditListener = context
        }else{
            throw ClassCastException(
                "$context must implement EventEditListener"
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_event_details, container, false)
        setHasOptionsMenu(true)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        event_details_back.setOnClickListener {
            activity?.onBackPressed()
        }
        setValues()

//        if (activity is MainActivity){
//            activity.showBottomNav(true)
//        }

        button_edit_event.setOnClickListener{
            activity?.onBackPressed()
            eventEditListener.eventEditClicked(event)

        }
        button_delete_event.setOnClickListener{
            val eventRef = event.eventRef
            DatabaseManager().getDatabase().collection("events").document(eventRef).delete()
                .addOnSuccessListener {
                    if (activity is SubjectDetails){
                        (activity as SubjectDetails).deleteSubEvent(eventRef)
                    }
                    activity?.onBackPressed()
                }


//            Toast.makeText(context, "${event.title} deleted", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.option_about_app)?.isVisible = false
        menu.findItem(R.id.option_logout)?.isVisible = false
    }
    private fun setValues(){
        event_details_title.text = getString(R.string.event_details_string, event.title)
        event_details_type.text = getString(R.string.placeholder_string, event.type.name)
        event_details_start.text = getString(R.string.time_and_date,formatter.format(event.startTime),formatter2.format(event.startTime))
        event_details_end.text = getString(R.string.time_and_date,formatter.format(event.endTime),formatter2.format(event.endTime))
        event_details_notes.text = getString(R.string.placeholder_string,event.note)
    }



}
