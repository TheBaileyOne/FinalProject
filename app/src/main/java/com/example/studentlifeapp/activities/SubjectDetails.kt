package com.example.studentlifeapp.activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.fragments.AddEventFragment
import com.example.studentlifeapp.fragments.EventExpandFragment
import com.example.studentlifeapp.getColorCompat
import com.example.studentlifeapp.util.getJsonExtra
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_subject_details.*
import kotlinx.android.synthetic.main.subject_event_item_view.*
import org.threeten.bp.format.DateTimeFormatter

class SubjectEventsAdapter(private var events:List<Pair<String,List<Event>>>, val onItemClick: ((Pair<String,List<Event>>) -> Unit)?): RecyclerView.Adapter<SubjectEventsAdapter.SubjectEventsViewHolder>(){

    override fun onBindViewHolder(viewHolder: SubjectEventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }
    override fun getItemCount(): Int = events.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectEventsViewHolder {
        return SubjectEventsViewHolder(parent.inflate(R.layout.subject_event_item_view))
    }

    inner class SubjectEventsViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        init{
            itemView.setOnClickListener{
                onItemClick?.invoke(events[adapterPosition])
            }
        }
        //TODO: on click listener to open up event details page, with edit event allowance
        fun bind(event: Pair<String,List<Event>>) {
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
    fun refreshList(newEvents: List<Pair<String, List<Event>>>){
        events = newEvents
        this.notifyDataSetChanged()

    }
}

class SubjectDetails : AppCompatActivity(),AddEventFragment.OnEventSavedListener {
    //TODO:Finish implementing interface for communicating between fragment and activity
    private lateinit var recyclerView:RecyclerView
    private lateinit var viewAdapter: SubjectEventsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var subject: Subject
    private lateinit var subjectRef:String
    private val events = mutableListOf<Event>()
    private lateinit var listener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_details)
        subject = intent.getJsonExtra(Subject::class.java)
//        subjectRef = intent.getStringExtra("subRef")!!
        subjectRef = subject.getId()
        listener = subDbEventsListener()
        //TODO: sort out animation for activity opening
//        val events: MutableList<Event> = subject!!.events
        val eventsGroup = formatEvents(events)
        supportActionBar?.title = subject?.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewAdapter = SubjectEventsAdapter(eventsGroup){event:Pair<String,List<Event>> ->eventClicked(event)}

        subject_title_view_name.text = subject?.name
        subject_info_view_name.text = subject?.summary

        recyclerView = subject_events_recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        viewAdapter.notifyDataSetChanged()

        //add event button clicked
        subject_info_view_button_addEvent.setOnClickListener{
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = AddEventFragment(subject.subjectEnd)
            fragment.setOnEventSavedListener(this)
            fragmentTransaction.add(R.id.subject_detail_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }


    }

    private fun formatEvents(events:MutableList<Event>):List<Pair<String,List<Event>>>{
        events.sortBy { it.startTime }
        val eventsMap = events.groupBy { it.eventId }
        return eventsMap.toList()
    }

    private fun eventClicked(event:Pair<String,List<Event>>) {
        if (event.second.size>1){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = EventExpandFragment(event.second)
            fragmentTransaction.add(R.id.subject_detail_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
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

    override fun onEventSaved(events: MutableList<Event>) {
        subject?.addEvents(events)
        val db = DatabaseManager()
//        db.exportEvents(events)
        Toast.makeText(this, "${events.size} events added", Toast.LENGTH_SHORT).show()
//        val dbEvents: MutableList<Event> = mutableListOf()
//        db.getDatabase().collection("subjects").get()
//            .addOnSuccessListener {result ->
//                for (document in result){
//                    if (document !=null) {
//                        Log.d(ContentValues.TAG, "${document.id}=> ${document.data}")
//                        val type = if(document.getString("type")!=null)  else EventType.LECTURE
//                        // val event = document.toObject(Event::class.java)
//                        val event = Event(
//                            title = document.getString("title")!!,
//                            type = EventType.valueOf(document.getString("type")!!),
//                            startTime = (document.get("start_time") as Timestamp).tolocalDateTime(),
//                            endTime = (document.get("end_time") as Timestamp).tolocalDateTime(),
//                            note = document.getString("note"),
//                            eventId = document.getString("eventId")!!
//                        )
//                        dbEvents.add(event)
//                    }
//                }
//
//                val eventsGroup = formatEvents(dbEvents)
//                viewAdapter.refreshList(eventsGroup)
//            }
//            .addOnFailureListener{e->
//                Log.d(ContentValues.TAG,"Error getting documents: ", e)
//            }
    }
    private fun subDbEventsListener():ListenerRegistration{
        Log.d("subDbEventsListenerCalled","subDbEventsListener called")
        val db = DatabaseManager()
        val dbEvents: MutableList<Event> = mutableListOf()
        return db.getDatabase().collection("subjects").document(subjectRef).collection("eventRef")
            .addSnapshotListener { snapshot, e ->

                Log.d("subDbEventsListenerCalled","Snapshot Retrieved")
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                for(docChange in snapshot!!.documentChanges){
                    Log.d("DocChange", "docChange = ${docChange.document.getString("ref")}")
                    val eventId = docChange.document.getString("ref")!!
                    db.getDatabase().collection("events").document(eventId).get()
//                    db.getDatabase().collection("eventRef").document(docChange.document.getString("ref")!!).get()
                        .addOnSuccessListener {event ->
                            Log.d(TAG, "Success")
                            if(event!=null){
//                                Log.d("EventChanged", "${event.id} => ${event.data}")
                                dbEvents.add(Event(
                                title = event.getString("title")!!,
                                type = EventType.valueOf(event.getString("type")!!),
                                startTime = (event.get("start_time") as Timestamp).tolocalDateTime(),
                                endTime = (event.get("end_time") as Timestamp).tolocalDateTime(),
                                note = event.getString("note"),
                                eventId = event.getString("eventId")!!))
                                }
                            val eventsGroup = formatEvents(dbEvents)
                            viewAdapter.refreshList(eventsGroup)
                        }
                        .addOnFailureListener{e ->
                            Log.w(TAG, "get collection fail, Error: $e")
                        }
                }
//                Log.d("DbEvents","dbEvents = $dbEvents")


            }
    }


}

