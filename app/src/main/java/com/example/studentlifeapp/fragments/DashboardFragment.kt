package com.example.studentlifeapp.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.net.sip.SipSession
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.*
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.getColorCompat
import com.example.studentlifeapp.inflate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_item_view.*
import kotlinx.android.synthetic.main.fragment_add_assessment.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.LocalDateTime

class DashEventsAdapter(private var events: MutableList<Event> = mutableListOf(), val onClick:(Event)->Unit):RecyclerView.Adapter<DashEventsAdapter.DashViewHolder>(){
//  private var events: MutableList<Event> = mutableListOf()
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter2=DateTimeFormatter.ofPattern("EEE\ndd\nMMM")
    private val formatter3=DateTimeFormatter.ofPattern("EEE, dd MMM")
    fun setEvents(newEvents:MutableList<Event>){
        events = newEvents
//        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashViewHolder {
        return DashViewHolder(parent.inflate(R.layout.event_item_view))
    }
    override fun getItemCount(): Int = events.size
    override fun onBindViewHolder(holder: DashViewHolder, position: Int) {
        holder.bind(events[position])
    }

    inner class DashViewHolder(override val containerView:View):RecyclerView.ViewHolder(containerView),LayoutContainer{
        init{
            itemView.setOnClickListener{
                onClick(events[adapterPosition])
            }
        }
        fun bind(event:Event){
            event_view_title.text = event.title
            event_view_icon.setBackgroundColor(itemView.context.getColorCompat(event.colour))
//            event_view_icon.visibility = View.INVISIBLE
//            if (event.location?.basicDisplay() != null){
//                event_view_location.text = event.location?.basicDisplay()
//                event_view_location_icon.visibility = View.VISIBLE
//            }else{
//                event_view_location.text = ""
//                event_view_location_icon.visibility = View.INVISIBLE
//            }
            event_view_location_icon.visibility = View.GONE
            event_view_location.text = formatter3.format(event.startTime)
            event_view_time.text = "${formatter.format(event.startTime)}\n-\n${formatter.format(event.endTime)}"
//            event_view_icon_text.visibility = View.VISIBLE
//            event_view_icon_text.text = formatter2.format(event.startTime)

        }
    }
}



class DashboardFragment : Fragment() {
//    interface EventClickedListener{
//        fun eventClicked(event: Event)
//    }

//    private lateinit var  eventClickListener: EventClickedListener
//    private lateinit var upcomingRecyclerView: RecyclerView
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
    var  viewManager: RecyclerView.LayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    //ensure fragment actually attaches, and that activity implements interface


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        upcomingAdapter  = DashEventsAdapter(upcomingEvents){event:Event -> eventClicked(event)}
        reminderAdapter  = DashEventsAdapter(reminderEvents){event:Event -> eventClicked(event)}
        todayAdapter = DashEventsAdapter(todayEvents){event:Event -> eventClicked(event)}
        tomorrowAdapter  = DashEventsAdapter(tomorrowEvents){event:Event -> eventClicked(event)}
        nextAdapter = DashEventsAdapter(nextEvents){event:Event -> eventClicked(event)}
        next2Adapter  = DashEventsAdapter(next2Events){event:Event -> eventClicked(event)}
        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setRecyclerView(upcomingAdapter, deadlines_recycler_view)
//        setRecyclerView(tomorrowAdapter,tomorrow_recycler_view)
//        setRecyclerView(reminderAdapter,reminder_recycler_view)
//        setRecyclerView(nextAdapter,next_day_recycler_view)
//        setRecyclerView(next2Adapter,next_day_recycler_view_2)

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

//        viewManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
//        upcomingAdapter = DashEventsAdapter(upcomingEvents){event:Event -> eventClicked(event)}
//        upcomingRecyclerView = deadlines_recycler_view.apply {
//            layoutManager = viewManager
//            adapter = upcomingAdapter
//        }
//        upcomingRecyclerView.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
//        upcomingAdapter.notifyDataSetChanged()

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
    }

    private fun dashEventsListener():ListenerRegistration{
        val db = DatabaseManager().getDatabase().collection("events")
        val timestamp = LocalDateTime.now().minusHours(23).toTimeStamp()
        val dbOrder = db.orderBy("start_time")
        val dbQuery = dbOrder.whereGreaterThanOrEqualTo("start_time", timestamp)
        val dbUpcoming = mutableListOf<Event>()
        val dbToday = mutableListOf<Event>()
        val dbTomorrow = mutableListOf<Event>()
        val dbReminder = mutableListOf<Event>()
        val dbNext = mutableListOf<Event>()
        val dbNext2 = mutableListOf<Event>()
        var tomorrow = LocalDateTime.now().toLocalDate().plusDays(1)
        var firstDay: LocalDate? = null
        var secondDay: LocalDate? = null
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
        return dbQuery.addSnapshotListener{snapshot, e ->
//        return dbQuery.whereIn("type", mutableListOf("EXAM", "COURSEWORK")).addSnapshotListener{snapshot, e ->
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
                Log.d("Dash","Event: ${event.title}, Type: ${event.type}, date:${event.startTime.toLocalDate()}")
                if (event.type == EventType.EXAM || event.type == EventType.COURSEWORK) {
                    dbUpcoming.add(event)
                    placeholder_deadlines.visibility = View.GONE
                } else if (event.type == EventType.REMINDER) {
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
                        dashboard_next_events.visibility = View.VISIBLE
                        text_view_next_day_2.text = getString(R.string.placeholder_string, dateFormatter.format(event.startTime))
                    }
                    event.startTime.toLocalDate().isEqual(secondDay) -> {
                        dbNext2.add(event)
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
            dbNext.clear()
            dbNext2.clear()
            dbReminder.clear()
            dbToday.clear()
            dbTomorrow.clear()
            dbUpcoming.clear()


        }

    }
}
