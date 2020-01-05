package com.example.studentlifeapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.data.importEvents
import com.example.studentlifeapp.getColorCompat
import com.example.studentlifeapp.getJsonExtra
import com.example.studentlifeapp.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_study_mode.*
import kotlinx.android.synthetic.main.activity_subject_details.*
import kotlinx.android.synthetic.main.event_item_view.*
import kotlinx.android.synthetic.main.subject_event_item_view.*
import org.threeten.bp.format.DateTimeFormatter

class SubjectEventsAdapter(private val events:List<Pair<String,List<Event>>>): RecyclerView.Adapter<SubjectEventsAdapter.SubjectEventsViewHolder>(){

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onBindViewHolder(viewHolder: SubjectEventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }
    override fun getItemCount(): Int = events.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectEventsViewHolder {
        return SubjectEventsViewHolder(parent.inflate(R.layout.subject_event_item_view))
    }

    inner class SubjectEventsViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),


        LayoutContainer {
        //TODO: on click listener to open up event details page, with edit event allowance
        fun bind(event: Pair<String,List<Event>>) {
//            event_view_title.text = event.title
            val formatter = DateTimeFormatter.ofPattern("EEE")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            subject_event_view_title.text = event.first
            if (event.second.size > 1) subject_event_view_timeList.text = "Times..."
            else subject_event_view_timeList.text =
                "${timeFormatter.format(event.second[0].startTime)} - ${timeFormatter.format(event.second[0].endTime)}"

            subject_event_view_day.text = formatter.format(event.second[0].startTime.dayOfWeek)
            subject_event_view_icon.setBackgroundColor(itemView.context.getColorCompat(event.second[0].colour))

        }
    }
}

class SubjectDetails : AppCompatActivity() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_details)

        //TODO: sort out animation for activity opening
        val subject :Subject? = intent.getJsonExtra(Subject::class.java)
        val events: MutableList<Event> = subject!!.events
        events.sortBy { it.startTime }
        val eventsMap = events.groupBy{it.title}
        val eventsGroup = eventsMap.toList()
        supportActionBar?.title = subject?.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        viewAdapter = SubjectEventsAdapter(eventsGroup)

        subject_title_view_name.text = subject?.name
        subject_info_view_name.text = subject?.summary

        recyclerView = subject_events_recyclerView.apply{
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
        recyclerView.addItemDecoration(DividerItemDecoration(this,RecyclerView.VERTICAL))
        viewAdapter.notifyDataSetChanged()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit,menu)
        return true
    }
}

