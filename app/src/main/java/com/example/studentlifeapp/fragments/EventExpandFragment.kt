package com.example.studentlifeapp.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.getColorCompat
import com.example.studentlifeapp.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_item_view.*
import kotlinx.android.synthetic.main.fragment_event_expand.*
import kotlinx.android.synthetic.main.list_item.*
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ClassCastException

//class EventExpandAdapter(val events:List<Event>,val onClick: (Event) -> Unit): RecyclerView.Adapter<EventExpandAdapter.EventExpandViewHolder>(){
class EventExpandAdapter(val events:List<Event>): RecyclerView.Adapter<EventExpandAdapter.EventExpandViewHolder>(){


    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter2=DateTimeFormatter.ofPattern("EEE\ndd\nMMM")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventExpandViewHolder {
        return EventExpandViewHolder(parent.inflate(R.layout.event_item_view))
    }

    override fun onBindViewHolder(viewHolder: EventExpandViewHolder, position: Int) {
        viewHolder.bind(events[position])
//        viewHolder.bind(events[position], onClick)
    }

    override fun getItemCount(): Int = events.size


    //assigns or updates data in the view items (overwrites previous data if view reused)
    inner class EventExpandViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

//        init{
//            itemView.setOnClickListener{
//                onClick(events[adapterPosition])
//            }
//        }

        fun bind(event: Event) {
//        fun bind(event: Event, clickListener: (Event) -> Unit) {

            event_view_title.text = event.title
            event_view_icon.visibility = View.INVISIBLE
            if (event.location?.basicDisplay() != null){
                event_view_location.text = event.location?.basicDisplay()
                event_view_location_icon.visibility = View.VISIBLE
            }else{
                event_view_location.text = ""
                event_view_location_icon.visibility = View.INVISIBLE
            }

            event_view_time.text = "${formatter.format(event.startTime)}\n-\n${formatter.format(event.endTime)}"
            event_view_icon_text.visibility = View.VISIBLE
            event_view_icon_text.text = formatter2.format(event.startTime)

        }

    }


}

class EventExpandFragment(private val events: List<Event>) : Fragment() {

    interface EventClickedListener{
        fun eventClicked(event:Event)
    }

    private lateinit var eventClickListener: EventClickedListener
    private val eventAdapter = EventExpandAdapter(events)
//    private val eventAdapter = EventExpandAdapter(events){event:Event->eventClicked(event)}


//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if(context is EventClickedListener){
//            eventClickListener = context
//        }else{
//            throw ClassCastException(
//                "$context must implement EventClickListener")
//        }
//    }

//    private fun eventClicked(event:Event){
//        eventClickListener.eventClicked(event)
//    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_expand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        event_expanded_recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        event_expanded_recyclerView.adapter=eventAdapter
        event_expanded_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        eventAdapter.notifyDataSetChanged()

    }

}
