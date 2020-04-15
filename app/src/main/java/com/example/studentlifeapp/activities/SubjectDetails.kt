package com.example.studentlifeapp.activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.*
import com.example.studentlifeapp.fragments.*
import com.example.studentlifeapp.getColorCompat
import com.example.studentlifeapp.util.getJsonExtra
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.example.studentlifeapp.util.Utils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_subject_details.*
import kotlinx.android.synthetic.main.subject_event_item_view.*
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat

class SubjectEventsAdapter(private var events:List<Pair<String,List<Event>>>, val onItemClick: ((Pair<String,List<Event>>) -> Unit)?):
    RecyclerView.Adapter<SubjectEventsAdapter.SubjectEventsViewHolder>(){

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

class SubjectDetails : AppCompatActivity(),AddEventFragment.OnEventSavedListener, AddAssessmentFragment.OnAssessmentSavedListener,
    Utils.EventDetailClickListener{
    //TODO:Finish implementing interface for communicating between fragment and activity
    private lateinit var recyclerView:RecyclerView
    private lateinit var viewAdapter: SubjectEventsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var subject: Subject
    private lateinit var subjectRef:String
    private val events = mutableListOf<Event>()
    private val rows = mutableListOf<TableRow>()
    private var subjectPercentage = 0.0
    private lateinit var eventsListener: ListenerRegistration
    private lateinit var assessmentListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_details)
        setSupportActionBar(subjectToolbar)
        subject = intent.getJsonExtra("subject",Subject::class.java)!!
        subjectRef = subject.getId()
        eventsListener = subDbEventsListener()
        assessmentListener = subDbAssessmentsListener()

        //TODO: sort out animation for activity opening
        val eventsGroup = formatEvents(events)
        supportActionBar?.title = "Subject Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewAdapter = SubjectEventsAdapter(eventsGroup){event:Pair<String,List<Event>> ->eventClicked(event)}

        val formatter2= DateTimeFormatter.ofPattern("EEE, dd MMM")
        subject_title_view_name.text = getString(R.string.subject_details_name,subject?.name)
        subject_details_date.text = getString(R.string.date_span, formatter2.format(subject.subjectStart),formatter2.format(subject.subjectEnd))
        subject_info_view_details.text = subject?.summary

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
        subject_info_view_button_generateStudy.setOnClickListener{
            val dbEvents = mutableListOf<Event>()
            val user = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance().collection("users").document(user).collection("events")
            db.get()
                .addOnSuccessListener {documents ->
                    for (document in documents){
                        dbEvents.add(
                            Event(
                                title = document.getString("title")!!,
                                type = EventType.valueOf(document.getString("type")!!),
                                startTime = (document.get("start_time") as Timestamp).tolocalDateTime(),
                                endTime = (document.get("end_time") as Timestamp).tolocalDateTime(),
                                note = document.getString("note"),
                                eventId = document.getString("eventId")!!
                            )
                        )
                    }
//                    generateStudy(dbEvents)
                    val fragmentManager = this.supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    val fragment = GenerateStudiesFragment(dbEvents,subject)
                    fragmentTransaction.add(R.id.subject_detail_fragment, fragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()

                }
                .addOnFailureListener{e->
                    Log.w(TAG, "Error getting documentsL ", e)
                }
        }
        button_add_score.setOnClickListener{

            val fragmentManager = this.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = AddAssessmentFragment(subject.remainingWeight)
            fragment.setOnAssessmentSavedListener(this)
            fragmentTransaction.add(R.id.subject_detail_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
//            val assessment = Assessment("Test",70.0,100.0,50.0, EventType.EXAM)
//            addRow(assessment)
        }
    }

    private fun addRow(assessment: Assessment){
        val row = TableRow(this)
        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        row.layoutParams = layoutParams
        val nameText = TextView(this)
        nameText.text = assessment.name
        nameText.setPadding(4,4,4,4)
        val weightingText = TextView(this)
        weightingText.text = assessment.weighting.toString()
        weightingText.setPadding(4,4,4,4)
        weightingText.textAlignment = View.TEXT_ALIGNMENT_CENTER
//        val maxMarkText = TextView(this)
//        maxMarkText.text = assessment.maxMark.toString()
//        maxMarkText.setPadding(4,4,4,4)
        val markText = TextView(this)
        markText.text = getString(R.string.mark_out_of,assessment.mark,assessment.maxMark)
        markText.setPadding(4,4,4,4)
        markText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        val percentageText = TextView(this)
        val decFormat = DecimalFormat("#.00")
        percentageText.text = decFormat.format(assessment.calculatePercentage())
        percentageText.setPadding(4,4,4,4)
        percentageText.textAlignment = View.TEXT_ALIGNMENT_CENTER
        row.addView(nameText)
        row.addView(weightingText)
        row.addView(markText)
        row.addView(percentageText)
        table_subject_grades.addView(row)
        rows.add(row)
    }

    private fun tableTitleDisplay(visible: Boolean){
        if (visible){
            table_subject_grades_title.visibility = View.VISIBLE
        }else{
            table_subject_grades_title.visibility = View.GONE
        }

    }

    private fun displayTotalPercentage(visible: Boolean){
        if(visible){
            sub_percent_view.visibility = View.VISIBLE
        }else{
            sub_percent_view.visibility = View.GONE
        }
    }

    fun onMarkChange(){
        //TODO: Allow marks to change
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
        else{
            onEventClicked("SUBJECT_DETAILS", event.second[0])
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
    fun addStudies(studies:MutableList<Event>){
        subject.addEvents(studies)
        Toast.makeText(this, "${studies.size} studies added", Toast.LENGTH_SHORT).show()

    }
    override fun onEventSaved(events: MutableList<Event>) {
        subject.addEvents(events)
        Toast.makeText(this, "${events.size} events added", Toast.LENGTH_SHORT).show()

    }
    override fun onAssessmentSaved(assessment: Assessment) {
        subject.addAssessment(assessment)
        Toast.makeText(this, "assessment added",Toast.LENGTH_SHORT).show()

    }

    private fun subDbAssessmentsListener():ListenerRegistration{
        val db = DatabaseManager()
        val dbAssessments: MutableList<Assessment> = mutableListOf()
        return db.getDatabase().collection("subjects").document(subjectRef).collection("assessmentRef")
            .addSnapshotListener{snapshot, e ->
                if (e!= null){
                    Log.w(TAG, "assessment listen Failed:", e)
                    return@addSnapshotListener
                }
                tableTitleDisplay(true)
                for(docChange in snapshot!!.documentChanges){
                    val assessmentId = docChange.document.getString("ref")!!
                    db.getDatabase().collection("assessments").document(assessmentId).get()
                        .addOnSuccessListener { assessment ->
                            Log.d(TAG, "Assessment Retrieved")
                            if (assessment != null){
                                val assessment = Assessment(
                                    name = assessment.getString("name")!!,
                                    mark = assessment.getDouble("mark")!!,
                                    maxMark =  assessment.getDouble("maxMark")!!,
                                    weighting = assessment.getDouble("weighting")!!,
                                    type = EventType.valueOf(assessment.getString("type")!!)
                                )
                                dbAssessments.add(assessment)
                                addRow(assessment)
                                subject.remainingWeight -= assessment.weighting.toInt()
                                subjectPercentage += assessment.getWeightedPercentage()
                                displayTotalPercentage(true)
                                subject_percent.text = getString(R.string.subject_percent_string,subjectPercentage.toFloat())
                                subject.percentage = subjectPercentage
//                                val dataPercent = hashMapOf("percentage" to subject.percentage)
                                db.getDatabase().collection("subjects").document(subjectRef).update("percentage", subject.percentage)
                                    .addOnFailureListener{e->
                                        Log.w(TAG,"Error Adding Document")
                                    }
                            }
                        }
                        .addOnFailureListener{e ->
                            Log.w(TAG, "get collection fail, Error: $e")
                        }
                }
            }

    }

    private fun subDbEventsListener():ListenerRegistration{
        val db = DatabaseManager()
        val dbEvents: MutableList<Event> = mutableListOf()

        return db.getDatabase().collection("subjects").document(subjectRef).collection("eventRef")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                for(docChange in snapshot!!.documentChanges){
                    val eventId = docChange.document.getString("ref")!!
                    db.getDatabase().collection("events").document(eventId).get()
                        .addOnSuccessListener {event ->
                            Log.d(TAG, "Event Retrieved")
                            if(event!=null){
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
            }
    }

    override fun onEventClicked(tag: String, event: Event) {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EventDetailsFragment(event)
        fragmentTransaction.replace(R.id.subject_detail_fragment, fragment).addToBackStack("eventDetailsFrag").commit()
    }


}

