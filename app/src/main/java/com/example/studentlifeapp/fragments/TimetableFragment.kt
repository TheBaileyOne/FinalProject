package com.example.studentlifeapp.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.*
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.importEvents
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.setTextColorRes
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
import java.util.*



class EventAdapter:RecyclerView.Adapter<EventAdapter.EventsViewHolder>() {
    val events = mutableListOf<Event>()
//    private val baseView = R.layout.event_item_view
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(parent.inflate(R.layout.event_item_view))
    }

    override fun onBindViewHolder(viewHolder: EventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class EventsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(event: Event) {
//            val title = R.id.event_view_title as TextView
//            title.text = event.title

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

            //TODO: fix events objects/inheritance so that it is possible to access properties of all type of event

        }
    }
}


class TimetableFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    private var selectedDate: LocalDate? = null
    @RequiresApi(Build.VERSION_CODES.O)
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val eventAdapter = EventAdapter()
    @RequiresApi(Build.VERSION_CODES.O)
    private val events = importEvents().groupBy{ it.startTime.toLocalDate()}

    //TODO: private val events= [get events from database]

    override fun onViewCreated(view: View, savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        calendar_recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        calendar_recyclerView.adapter = eventAdapter
        calendar_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        eventAdapter.notifyDataSetChanged()

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()

        calendarView.setup(currentMonth.minusMonths(10),currentMonth.plusMonths(10), daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)
        class DayViewContainer(view:View):ViewContainer(view){
            lateinit var day: CalendarDay
            val textView = view.dayText
            val layout = view.dayLayout
//            val eventTopView = view.dayEventTop
//            val eventBottomView = view.dayEventBottom

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(it) }
                            updateAdapterForDate(day.date)
                        }
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

//                val eventTopView = container.eventTopView
//                val eventBottomView = container.eventBottomView

                if (day.owner == DayOwner.THIS_MONTH){
                    textView.setTextColorRes(R.color.colorPrimaryDark)
                    layout.setBackgroundResource(if (selectedDate==day.date) R.drawable.selected_day_background else 0)

                    val events =events[day.date]
                    if (events != null){
                       //TODO: do logic for displaying events

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
    }

    private fun updateAdapterForDate(date: LocalDate?) {
        eventAdapter.events.clear()
        eventAdapter.events.addAll(events[date].orEmpty())
        eventAdapter.notifyDataSetChanged()

    }

}
