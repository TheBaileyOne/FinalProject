package com.example.studentlifeapp.fragments

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.getColorCompat
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.util.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_item_view.*
import kotlinx.android.synthetic.main.fragment_event_expand.*
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ClassCastException

/**
 * Adapter for list of all originally grouped events
 */
class EventExpandAdapter(var events:List<Event>,val onClick: (Event) -> Unit): RecyclerView.Adapter<EventExpandAdapter.EventExpandViewHolder>(){


    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter3=DateTimeFormatter.ofPattern("EEE, dd MMM")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventExpandViewHolder {
        return EventExpandViewHolder(parent.inflate(R.layout.event_item_view))
    }

    override fun onBindViewHolder(viewHolder: EventExpandViewHolder, position: Int) {
        viewHolder.bind(events[position])
//        viewHolder.bind(events[position], onClick)
    }

    override fun getItemCount(): Int = events.size

    fun refreshList(newEvents:MutableList<Event>){
        events = newEvents.toMutableList()
        this.notifyDataSetChanged()

    }


    //assigns or updates data in the view items (overwrites previous data if view reused)
    inner class EventExpandViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        init{
            itemView.setOnClickListener{
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: Event) {

            event_view_title.text = event.title
            when(event.type){
                EventType.STUDY ->{
                    event_view_icon.setImageResource(R.drawable.icons8_study)
                }
                EventType.LECTURE ->{
                    event_view_icon.setImageResource(R.drawable.icons8_lecture)
                }
                EventType.EXAM ->{
                    event_view_icon.setImageResource(R.drawable.icons8_exam)
                }
                EventType.EVENT ->{
                    event_view_icon.setImageResource(R.drawable.icons8_event)
                }
                EventType.JOBSHIFT ->{
                    event_view_icon.setImageResource(R.drawable.icons8_work)
                }
                EventType.CLASS->{
                    event_view_icon.setImageResource(R.drawable.icons8_tutorial)
                }
                EventType.COURSEWORK->{
                    event_view_icon.setImageResource(R.drawable.icons8_coursework)
                }
                EventType.REMINDER->{
                    event_view_icon.setImageResource(R.drawable.icons8_reminder)
                }
                EventType.SOCIETY->{
                    event_view_icon.setImageResource(R.drawable.icons8_society)
                }
            }

            event_view_icon.setColorFilter(itemView.context.getColorCompat(event.colour),
                PorterDuff.Mode.SRC_IN)

//            event_view_location_icon.visibility = View.GONE
            event_view_location.text = formatter3.format(event.startTime)
            event_view_time.text = "${formatter.format(event.startTime)}\n-\n${formatter.format(event.endTime)}"

        }

    }


}

class EventExpandFragment(private val groupRef:String, val title:String) : Fragment(){
//class EventExpandFragment(private val events: List<Event>) : Fragment(){

    interface EventClickedListener{
        fun eventClicked(event:Event)
    }
    private var events = mutableListOf<Event>()

    private lateinit var eventDetailClickListener: Utils.EventDetailClickListener
//    private val eventAdapter = EventExpandAdapter(events)

    private val eventAdapter = EventExpandAdapter(events){event:Event->eventClicked(event)}

    private lateinit var eventsViewModel: EventsViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Utils.EventDetailClickListener){
            eventDetailClickListener = context
        }else{
            throw ClassCastException(
                "$context must implement EventDetailClickListener"
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_event_expand, container, false)
        eventsViewModel = activity?.run{
            ViewModelProviders.of(this).get(EventsViewModel::class.java)
        } ?:throw Exception("Invalid Activity")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        event_expand_back.setOnClickListener {
            activity?.onBackPressed()
        }
        event_expand_title.text = getString(R.string.event_occurrences, title)
        event_expanded_recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        event_expanded_recyclerView.adapter=eventAdapter
        event_expanded_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))

        eventsViewModel.events.observe(viewLifecycleOwner, Observer { viewEvents ->
            viewEvents.sortBy { it.startTime }
            events = viewEvents.filter { it.eventId == groupRef }.toMutableList()
            eventAdapter.refreshList(events)
        })

        eventAdapter.notifyDataSetChanged()

    }
    private fun eventClicked(event:Event){
//        activity?.onBackPressed()
        eventDetailClickListener.onEventClicked("TIMETABLE", event)
    }
}
