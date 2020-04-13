package com.example.studentlifeapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.EventLog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.data.Event
import kotlinx.android.synthetic.main.fragment_event_details.*
import org.threeten.bp.format.DateTimeFormatter

class EventDetailsFragment(val event: Event) : Fragment() {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter2= DateTimeFormatter.ofPattern("EEE, dd MMM")



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        event_details_back.setOnClickListener {
            activity?.onBackPressed()
        }
        event_details_title.text = getString(R.string.event_details_string, event.title)
        event_details_type.text = getString(R.string.placeholder_string, event.type.name)
        event_details_start.text = getString(R.string.time_and_date,formatter.format(event.startTime),formatter2.format(event.startTime))
        event_details_end.text = getString(R.string.time_and_date,formatter.format(event.endTime),formatter2.format(event.endTime))
        event_details_notes.text = getString(R.string.placeholder_string,event.note)

//        if (activity is MainActivity){
//            activity.showBottomNav(true)
//        }

        button_edit_event.setOnClickListener{
            Toast.makeText(context, "Edit text pressed", Toast.LENGTH_SHORT).show()
        }

    }



}
