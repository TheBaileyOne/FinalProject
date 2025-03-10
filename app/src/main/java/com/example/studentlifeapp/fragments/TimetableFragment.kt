package com.example.studentlifeapp.fragments

import android.content.Context
 import android.graphics.PorterDuff
 import android.os.Bundle
 import android.util.TypedValue
 import android.view.LayoutInflater
 import android.view.View
 import android.view.ViewGroup
 import android.widget.TextView
 import android.widget.Toast
 import androidx.core.view.children
 import androidx.core.view.marginBottom
 import androidx.fragment.app.Fragment
 import androidx.lifecycle.ViewModelProviders
 import androidx.recyclerview.widget.DividerItemDecoration
 import androidx.recyclerview.widget.LinearLayoutManager
 import androidx.recyclerview.widget.RecyclerView
 import com.example.studentlifeapp.*
 import com.example.studentlifeapp.data.Event
 import com.example.studentlifeapp.data.EventType
 import com.example.studentlifeapp.util.Utils
 import com.google.firebase.auth.FirebaseAuth
 import com.google.firebase.firestore.CollectionReference
 import com.google.firebase.firestore.FirebaseFirestore
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
 import kotlinx.android.synthetic.main.calendar_header.view.*
 import kotlinx.android.synthetic.main.event_item_view.*
 import kotlinx.android.synthetic.main.fragment_timetable.*
 import org.threeten.bp.LocalDate
 import org.threeten.bp.YearMonth
 import org.threeten.bp.format.DateTimeFormatter
 import org.threeten.bp.format.TextStyle
 import java.util.*

/**
 * Adapter for display events to display under calendar
 */
class EventAdapter(val onClick: (Event) -> Unit): RecyclerView.Adapter<EventAdapter.EventsViewHolder>(){

    val events = mutableListOf<Event>() //Data source for the adapter
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    private val formatter3=DateTimeFormatter.ofPattern("EEE, dd MMM")


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

            event_view_icon.setColorFilter(itemView.context.getColorCompat(event.colour),PorterDuff.Mode.SRC_IN)

            event_view_location.text = formatter3.format(event.startTime)
            event_view_time.text = "${formatter.format(event.startTime)}\n-\n${formatter.format(event.endTime)}"

            event_view_time.text = when (event.type) {
                EventType.REMINDER -> formatter.format(event.startTime)
                else -> "${formatter.format(event.startTime)}\n-\n${formatter.format(event.endTime)}"
            }
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
        eventDetailClickListener.onEventClicked("TIMETABLE", event)

    }

    override fun onViewCreated(view: View, savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)

        calendar_recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        calendar_recyclerView.adapter = eventAdapter
        calendar_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        eventAdapter.notifyDataSetChanged()

        eventViewModel.events.observe(viewLifecycleOwner, androidx.lifecycle.Observer { events ->
            groupedEvents = events.groupBy { it.startTime.toLocalDate() }.toMutableMap()
            calendarView.notifyCalendarChanged()
            updateAdapterForDate(today)
        })

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()

        calendarView.setup(currentMonth.minusMonths(10),currentMonth.plusMonths(10), daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        if(savedInstanceState == null){
            calendarView.post {
                selectDate(today)
            }
        }


        /**
         * Class for configuring the layout of the calendar
         */
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

    /**
     * Display events for the relevent date
     */
    private fun updateAdapterForDate(date: LocalDate?) {
        eventAdapter.events.clear()
        eventAdapter.events.addAll(groupedEvents[date].orEmpty())
        eventAdapter.notifyDataSetChanged()
    }

}
