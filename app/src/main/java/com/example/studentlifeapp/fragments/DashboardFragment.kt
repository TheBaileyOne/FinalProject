package com.example.studentlifeapp.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.getColorCompat
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.example.studentlifeapp.util.Utils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_item_view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class DashEventsAdapter(private var events: MutableList<Event> = mutableListOf(), val onClick:(Event)->Unit):RecyclerView.Adapter<DashEventsAdapter.DashViewHolder>(){
//  private var events: MutableList<Event> = mutableListOf()
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter3=DateTimeFormatter.ofPattern("EEE, dd MMM")
    fun setEvents(newEvents:MutableList<Event>){
        events = newEvents
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashViewHolder {
        return DashViewHolder(parent.inflate(R.layout.event_item_view))
    }
    override fun getItemCount(): Int = events.size
    override fun onBindViewHolder(holder: DashViewHolder, position: Int) {
        holder.bind(events[position])
    }

    fun clearEvents(){
        events.clear()
    }

    inner class DashViewHolder(override val containerView:View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        init{
            itemView.setOnClickListener{
                onClick(events[adapterPosition])
            }
        }
        fun bind(event:Event){
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

class DashboardFragment : Fragment() {
    private lateinit var upcomingAdapter: DashEventsAdapter
    private lateinit var reminderAdapter: DashEventsAdapter
    private lateinit var todayAdapter: DashEventsAdapter
    private lateinit var tomorrowAdapter: DashEventsAdapter
    private lateinit var nextAdapter: DashEventsAdapter
    private lateinit var next2Adapter: DashEventsAdapter
    private var upcomingEvents = mutableListOf<Event>()
    private var reminderEvents = mutableListOf<Event>()
    private var todayEvents = mutableListOf<Event>()
    private var tomorrowEvents = mutableListOf<Event>()
    private var nextEvents = mutableListOf<Event>()
    private var next2Events = mutableListOf<Event>()
    private lateinit var listener: ListenerRegistration
    private lateinit var eventDetailClickListener: Utils.EventDetailClickListener
    private lateinit var eventViewModel:EventsViewModel
    private var events = mutableListOf<Event>()

    //ensure fragment actually attaches, and that activity implements interface
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        upcomingAdapter  = DashEventsAdapter(upcomingEvents){event:Event -> eventClicked(event)}
        reminderAdapter  = DashEventsAdapter(reminderEvents){event:Event -> eventClicked(event)}
        todayAdapter = DashEventsAdapter(todayEvents){event:Event -> eventClicked(event)}
        tomorrowAdapter  = DashEventsAdapter(tomorrowEvents){event:Event -> eventClicked(event)}
        nextAdapter = DashEventsAdapter(nextEvents){event:Event -> eventClicked(event)}
        next2Adapter  = DashEventsAdapter(next2Events){event:Event -> eventClicked(event)}
        eventViewModel = activity?.run {
            ViewModelProviders.of(this).get(EventsViewModel::class.java)
        } ?: throw Exception ("Invalid Activity")
        eventViewModel.setEvents(events)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        events.clear()
        upcomingEvents.clear()
        reminderEvents.clear()
        todayEvents.clear()
        tomorrowEvents.clear()
        next2Events.clear()
        nextEvents.clear()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deadlines_recycler_view.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        deadlines_recycler_view.adapter = upcomingAdapter
        deadlines_recycler_view.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        upcomingAdapter.notifyDataSetChanged()

        today_recycler_view.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        today_recycler_view.adapter = todayAdapter
        today_recycler_view.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        todayAdapter.notifyDataSetChanged()

        tomorrow_recycler_view.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        tomorrow_recycler_view.adapter = tomorrowAdapter
        tomorrow_recycler_view.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        tomorrowAdapter.notifyDataSetChanged()

        reminder_recycler_view.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        reminder_recycler_view.adapter = reminderAdapter
        reminder_recycler_view.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        reminderAdapter.notifyDataSetChanged()

        next_day_recycler_view.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        next_day_recycler_view.adapter = nextAdapter
        next_day_recycler_view.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        nextAdapter.notifyDataSetChanged()

        next_day_recycler_view_2.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        next_day_recycler_view_2.adapter = next2Adapter
        next_day_recycler_view_2.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        next2Adapter.notifyDataSetChanged()

        listener = dashEventsListener()

        button_dash_study.setOnClickListener{
            upcomingAdapter.setEvents(mutableListOf(Event("View Test", EventType.EXAM, LocalDateTime.now())))
            upcomingAdapter.notifyDataSetChanged()
        }

    }


    private fun updateRecyclerView(adapter: DashEventsAdapter, events:MutableList<Event>, recyclerView:RecyclerView){
        if(events.isNotEmpty()){
            adapter.setEvents(events)
            adapter.notifyDataSetChanged()
        }
    }

    private fun eventClicked(event:Event){
        Toast.makeText(context, "${event.title} pressed", Toast.LENGTH_SHORT).show()
        eventDetailClickListener.onEventClicked("TIMETABLE", event)
    }

    private fun dashEventsListener():ListenerRegistration{
        val db = DatabaseManager().getDatabase().collection("events")
        val dbOrder = db.orderBy("start_time")
        var dbEvents = mutableListOf<Event>()
        return dbOrder.addSnapshotListener{snapshot, e ->
            if (e!=null){
                Log.w(TAG, "Dashboard Listen Failed", e)
                return@addSnapshotListener
            }
            for(docChange in snapshot!!.documentChanges) {
                val event = Event(
                    title = docChange.document.getString("title")!!,
                    type = EventType.valueOf(docChange.document.getString("type")!!),
                    startTime = (docChange.document.get("start_time") as Timestamp).tolocalDateTime(),
                    endTime = (docChange.document.get("end_time") as Timestamp).tolocalDateTime(),
                    note = docChange.document.getString("note"),
                    eventId = docChange.document.getString("eventId")!!
                )
                event.setRef(docChange.document.id)
                if(docChange.type == DocumentChange.Type.ADDED){
                    dbEvents.add(event)
//                    eventViewModel.addEvent(event)
                    events.add(event)
                }else if (docChange.type == DocumentChange.Type.MODIFIED){
                    if(events.any{it.eventRef == event.eventRef}){
                        val foundEvent = events.find { it.eventRef == event.eventRef}!!
                        val index = events.indexOf(foundEvent)
                        events[index] = event
                    }
                    else{
                        Log.d("ERROR", "Modified event should be found in the list.")
                    }
                }
                else if (docChange.type == DocumentChange.Type.REMOVED){
                    events.removeIf{it.eventRef == event.eventRef}
                }
            }

            eventViewModel.setEvents(events.toMutableList())
            arrangeEventLists(eventViewModel.getEvents()!!)
        }

    }

    private fun arrangeEventLists(events: MutableList<Event>){
        val dbUpcoming = mutableListOf<Event>()
        val dbToday = mutableListOf<Event>()
        val dbTomorrow = mutableListOf<Event>()
        val dbReminder = mutableListOf<Event>()
        val dbNext = mutableListOf<Event>()
        val dbNext2 = mutableListOf<Event>()

        placeholder_today.visibility = View.VISIBLE
        placeholder_tomorrow.visibility = View.VISIBLE
        placeholder_deadlines.visibility = View.VISIBLE
        placeholder_reminders.visibility = View.VISIBLE



        reminderAdapter.clearEvents()
        nextAdapter.clearEvents()
        next2Adapter.clearEvents()
        upcomingAdapter.clearEvents()
        tomorrowAdapter.clearEvents()
        todayAdapter.clearEvents()


        var tomorrow = LocalDateTime.now().toLocalDate().plusDays(1)
        var firstDay: LocalDate? = null
        var secondDay: LocalDate? = null
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
        for (event in events){
            if(event.startTime.toLocalDate().isAfter(LocalDate.now()) or event.startTime.toLocalDate().isEqual(LocalDate.now())){
                if (event.type == EventType.EXAM || event.type == EventType.COURSEWORK) {
                    dbUpcoming.add(event)
                    placeholder_deadlines.visibility = View.GONE
                }
                else if (event.type == EventType.REMINDER) {
                    dbReminder.add(event)
                    placeholder_reminders.visibility = View.GONE
                }
                when {
                    event.startTime.toLocalDate().isEqual(LocalDate.now()) -> {
                        dbToday.add(event)
                        placeholder_today.visibility = View.GONE
                    }
                    event.startTime.toLocalDate().isEqual(tomorrow) -> {
                        dbTomorrow.add(event)
                        placeholder_tomorrow.visibility = View.GONE
                    }
                    firstDay == null -> {
                        firstDay = event.startTime.toLocalDate()
                        dbNext.add(event)
                        dashboard_next_events.visibility = View.VISIBLE
                        text_view_next_day.text = getString(R.string.placeholder_string, dateFormatter.format(event.startTime))
                    }
                    event.startTime.toLocalDate().isEqual(firstDay) -> {
                        dbNext.add(event)
                    }
                    secondDay == null -> {
                        secondDay = event.startTime.toLocalDate()
                        dbNext2.add(event)
                        dashboard_next_events_2.visibility = View.VISIBLE
                        text_view_next_day_2.text = getString(R.string.placeholder_string, dateFormatter.format(event.startTime))
                    }
                    event.startTime.toLocalDate().isEqual(secondDay) -> {
                        dbNext2.add(event)
                    }
                }
            }
        }
        nextEvents = dbNext.toMutableList()
        next2Events= dbNext2.toMutableList()
        reminderEvents = dbReminder.toMutableList()
        todayEvents = dbToday.toMutableList()
        tomorrowEvents = dbTomorrow.toMutableList()
        upcomingEvents = dbUpcoming.toMutableList()
        updateRecyclerView(upcomingAdapter,upcomingEvents,deadlines_recycler_view)
        updateRecyclerView(todayAdapter,todayEvents, today_recycler_view)
        updateRecyclerView(tomorrowAdapter,tomorrowEvents,tomorrow_recycler_view)
        updateRecyclerView(reminderAdapter,reminderEvents,reminder_recycler_view)
        updateRecyclerView(nextAdapter,nextEvents,next_day_recycler_view)
        updateRecyclerView(next2Adapter,next2Events,next_day_recycler_view_2)
    }
}

class EventsViewModel : ViewModel(){
    val events: MutableLiveData<MutableList<Event>> = MutableLiveData()
    fun getEvents() = events.value
    fun setEvents(events:MutableList<Event>){
        this.events.value = events
    }
}
