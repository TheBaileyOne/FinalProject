package com.example.studentlifeapp.fragments

 import android.content.ContentValues
import android.content.ContentValues.TAG
 import android.content.Context
 import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.fragment.app.FragmentManager
 import androidx.lifecycle.ViewModelProvider
 import androidx.lifecycle.ViewModelProviders
 import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.*
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.setTextColorRes
 import com.example.studentlifeapp.util.Utils
 import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.calendar_header.view.*
import kotlinx.android.synthetic.main.event_item_view.*
//import kotlinx.android.synthetic.main.event_item_view.view.*
import org.threeten.bp.LocalDate
//import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
 import java.lang.ClassCastException
 import java.util.*

//TODO: include FAB button or another thing to add an event
class EventAdapter(val onClick: (Event) -> Unit): RecyclerView.Adapter<EventAdapter.EventsViewHolder>(){

    val events = mutableListOf<Event>() //Data source for the adapter
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(parent.inflate(R.layout.event_item_view))
    }

    override fun onBindViewHolder(viewHolder: EventsViewHolder, position: Int) {
        viewHolder.bind(events[position], onClick)
    }

    override fun getItemCount(): Int = events.size


    //assigns or updates data in the view items (overwrites previous data if view reused)
    inner class EventsViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        init{
            itemView.setOnClickListener{
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: Event, clickListener: (Event) -> Unit) {

            event_view_title.text = event.title
            event_view_icon.setBackgroundColor(itemView.context.getColorCompat(event.colour))
            if (event.location?.basicDisplay() != null){
                event_view_location.text = event.location?.basicDisplay()
                event_view_location_icon.visibility = View.VISIBLE
            }else{
                event_view_location.text = ""
                event_view_location_icon.visibility = View.INVISIBLE
            }

            event_view_time.text = when (event.type) {
                EventType.REMINDER -> formatter.format(event.startTime)
                else -> "${formatter.format(event.startTime)}\n-\n${formatter.format(event.endTime)}"
            }
//            event_view.strokeColor = itemView.context.getColorCompat(event.colour)
        }

    }
}


class TimetableFragment : Fragment() {



    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val eventAdapter = EventAdapter{event:Event-> eventClicked(event)}
    private lateinit var user: String
    private lateinit var db: CollectionReference
//    private val dbEvents = mutableListOf<Event>()
//    private var tempStore = mutableListOf<Event>()
//    private var storeBool:Boolean = false
//    private var groupedEvents = dbEvents.groupBy{ it.startTime.toLocalDate()}.toMutableMap()
    private lateinit var groupedEvents:MutableMap<LocalDate, List<Event>>
    private lateinit var listener: ListenerRegistration
    private lateinit var eventDetailClickListener: Utils.EventDetailClickListener

    private lateinit var eventViewModel:EventsViewModel
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
        val view = inflater.inflate(R.layout.fragment_timetable, container, false)
        user = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseFirestore.getInstance().collection("users").document(user).collection("events")
        eventViewModel = activity?.run{
            ViewModelProviders.of(this).get(EventsViewModel::class.java)
        }?: throw Exception("Invalid Activity")
        groupedEvents =  eventViewModel.events.value?.groupBy{it.startTime.toLocalDate()}!!.toMutableMap()
        return view
    }

    //what to do when event clicked
    private fun eventClicked(event: Event){
        Toast.makeText(activity,"Clicked: ${event.title}", Toast.LENGTH_LONG).show()
        eventDetailClickListener.onEventClicked("TIMETABLE", event)

    }
    override fun onPause() {
        super.onPause()
//        listener.remove()
    }

    override fun onStop() {
        super.onStop()
//        listener.remove()
    }

    fun clearTimetable(){
//        dbEvents.clear()
        groupedEvents.clear()
        calendarView.notifyCalendarChanged()
//        listener.remove()
        Log.d("Clear","events cleared")

    }
    override fun onViewCreated(view: View, savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        Log.d("User", "Current User: ${FirebaseAuth.getInstance().currentUser?.email}")
//        Log.d("User","User events = ${dbEvents.size}")
//        listener = dbListener()

        calendar_recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        calendar_recyclerView.adapter = eventAdapter
        calendar_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        eventAdapter.notifyDataSetChanged()

        eventViewModel.events.observe(this, androidx.lifecycle.Observer { events ->
            groupedEvents = events.groupBy { it.startTime.toLocalDate() }.toMutableMap()
            calendarView.notifyCalendarChanged()
            updateAdapterForDate(today)
        })
//        fab_add.setOnClickListener {
//            val fragmentTransaction = fragmentManager?.beginTransaction()
//            val fragment = AddEventFragment()
//            fragment.setOnEventSavedListener(activity as MainActivity)
//            fragmentTransaction?.replace(R.id.view_pager_container, fragment)?.addToBackStack(null)
//                ?.commit()
//            (activity as MainActivity).showBottomNav(false)
//            storeBool = true
//        }

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()

        calendarView.setup(currentMonth.minusMonths(10),currentMonth.plusMonths(10), daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        if(savedInstanceState == null){
            calendarView.post {
                selectDate(today)
            }
        }



        class DayViewContainer(view:View):ViewContainer(view){
            lateinit var day: CalendarDay
            val textView = view.dayText
            val layout = view.dayLayout
            val eventView1 = view.dayEvent1
            val eventView2 = view.dayEvent2
            val eventView3 = view.dayEvent3
            val eventViewExtra = view.dayEventExtra



            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        calendarView.dayBinder = object : DayBinder<DayViewContainer>{
            override fun create(view:View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay){
                container.day = day
                val textView = container.textView
                val layout = container.layout
                textView.text = day.date.dayOfMonth.toString()

                val eventView1 = container.eventView1
                val eventView2 = container.eventView2
                val eventView3 = container.eventView3
                val eventViewExtra = container.eventViewExtra

                eventView1.background = null
                eventView2.background = null
                eventView3.background = null
                eventViewExtra.visibility = View.GONE


                if (day.owner == DayOwner.THIS_MONTH){
                    textView.setTextColorRes(R.color.colorPrimaryDark)
                    layout.setBackgroundResource(if (selectedDate==day.date) R.drawable.selected_day_background else 0)

                    val events =groupedEvents[day.date]
                    if (events != null){
                        when {
                            events.count() == 1 -> {
                                eventView3.setBackgroundColor(view.context.getColorCompat(events[0].colour))
                                eventView3.marginBottom
                            }
                            events.count()==2 -> {
                                eventView2.setBackgroundColor(view.context.getColorCompat(events[1].colour))
                                eventView3.setBackgroundColor(view.context.getColorCompat(events[0].colour))

                            }
                            events.count()==3 -> {
                                eventView1.setBackgroundColor(view.context.getColorCompat(events[0].colour))
                                eventView2.setBackgroundColor(view.context.getColorCompat(events[1].colour))
                                eventView3.setBackgroundColor(view.context.getColorCompat(events[2].colour))
                            }
                            else -> {
                                eventView1.setBackgroundColor(view.context.getColorCompat(events[0].colour))
                                eventView2.setBackgroundColor(view.context.getColorCompat(events[1].colour))
                                eventView3.setBackgroundColor(view.context.getColorCompat(events[2].colour))
                                val countString = "+ ${events.count() - 3}"
                                eventViewExtra.text = countString
                                eventViewExtra.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    //TODO: set colour
                    layout.background = null
                }
            }
        }
        class MonthViewContainer(view: View) : ViewContainer(view){
            val legendLayout = view.legendLayout
        }

        calendarView.monthHeaderBinder = object: MonthHeaderFooterBinder<MonthViewContainer>{
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container:MonthViewContainer, month: CalendarMonth) {
                if (container.legendLayout.tag == null){
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map{it as TextView}.forEachIndexed { index, tView ->
                        tView.text =
                            daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                .toUpperCase(
                                    Locale.ENGLISH
                                )
                        tView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    }
                    month.yearMonth
                }

            }
        }

        calendarView.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            monthYearText.text = title
            selectedDate?.let{
                selectedDate = null
                calendarView.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }
        nextMonthImage.setOnClickListener{
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.next)
            }
        }
        previousMonthImage.setOnClickListener{
            calendarView.findFirstVisibleMonth()?.let{
                calendarView.smoothScrollToMonth(it.yearMonth.previous)
            }
        }
        calendarView.notifyCalendarChanged()
    }

    private fun selectDate(date:LocalDate){
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            calendarView.notifyDateChanged(date)
            oldDate?.let { calendarView.notifyDateChanged(it) }
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate?) {
        eventAdapter.events.clear()
        eventAdapter.events.addAll(groupedEvents[date].orEmpty())
        eventAdapter.notifyDataSetChanged()

    }

//    private fun dbListener(): ListenerRegistration {
//        val dbEvents = mutableListOf<Event>()
//
//        return db.addSnapshotListener{ value, e ->
//            if (e!= null){
//                Log.w(TAG, "snapshot listen failed.",e)
//                return@addSnapshotListener
//            }
//            val source = if (value != null && value.metadata.hasPendingWrites()) "local" else "server"
//
//            for (docChange in value!!.documentChanges){
//                Log.d("Doc Change", "$source doc of type: ${docChange.type}")
//                val event = Event(
//                    title = docChange.document.getString("title")!!,
//                    type = EventType.valueOf(docChange.document.getString("type")!!),
//                    startTime = (docChange.document.get("start_time") as Timestamp).tolocalDateTime(),
//                    endTime = (docChange.document.get("end_time") as Timestamp).tolocalDateTime(),
//                    note = docChange.document.getString("note"),
//                    eventId = docChange.document.getString("eventId")!!
//                )
//                dbEvents.add(event)
//                event.setRef(docChange.document.id)
//
//            }
//            this.dbEvents.addAll(dbEvents)
//            events = dbEvents.groupBy { it.startTime.toLocalDate() }.toMutableMap()
//            Log.d(TAG, "Events updated, number of Events: ${dbEvents.size}")
//            dbEvents.clear()
//            calendarView.notifyCalendarChanged()
//            updateAdapterForDate(today)
//
//        }
//    }

}
