package com.example.studentlifeapp.fragments

import android.content.ContentValues
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.StudyMode
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.importEvents
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.example.studentlifeapp.util.PrefUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_study_mode.*
import kotlinx.android.synthetic.main.fragment_study_mode.view.*
import kotlinx.android.synthetic.main.study_item.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

class StudyAdapter(private var studies:List<Event> = mutableListOf(), val onClick: (Event)-> Unit): RecyclerView.Adapter<StudyAdapter.StudyViewHolder>(){
    override fun getItemCount(): Int = studies.size

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.bind(studies[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        return StudyViewHolder(parent.inflate(R.layout.study_item))
    }
    inner class StudyViewHolder(override val containerView:View):RecyclerView.ViewHolder(containerView), LayoutContainer{
        init {
            itemView.setOnClickListener{
                onClick(studies[adapterPosition])
            }
        }
        fun bind(study:Event){
            val formatter = DateTimeFormatter.ofPattern("EEE d LLL - HH:mm")
            study_item_title.text = study.title
//            study_item_description.text= study.note
            study_item_description.text= formatter.format(study.startTime)
            study_item_icon.visibility = View.GONE
            val difference =Math.toIntExact (ChronoUnit.MINUTES.between(study.startTime, study.endTime))
            study_item_time.text = "$difference\nmins"
        }
    }

    fun refreshList(newStudies: List<Event>){
        studies = newStudies
        this.notifyDataSetChanged()
    }


}

class StudyModeFragment : Fragment() {
//TODO: make it run a study mode

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: StudyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var events = mutableListOf<Event>()
//    private var events = mutableListOf<Event>()
    private lateinit var listener: ListenerRegistration
//    private lateinit var studies: List<Event>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_study_mode, container, false)


        view.studyButtonManual.setOnClickListener{
            Log.d("studyButton","Selected")
            //TODO: pass length for timer/pass Study object length
            val intent = Intent(context, StudyMode::class.java)
            startActivity(intent)

        }
        return view

    }
    //TODO: Sort out list reloading, and make list fit in screen better
    override fun onPause() {
        super.onPause()
        listener.remove()
    }

    override fun onStop() {
        super.onStop()
        listener.remove()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = studyDbListener()

//        studies = events.filter{
//            it.type == EventType.STUDY
//        }

        viewManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewAdapter = StudyAdapter(events){study:Event ->studyClicked(study)}
//        viewAdapter = StudyAdapter(studies){study:Event ->studyClicked(study)}

        recyclerView = study_recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        viewAdapter.notifyDataSetChanged()

    }

    private fun studyClicked(study:Event){
        val intent = Intent(context, StudyMode::class.java)
        val difference = ChronoUnit.MINUTES.between(study.startTime, study.endTime).toInt()
        Toast.makeText(context, "$difference mins", Toast.LENGTH_SHORT).show()
        intent.putExtra("timer_length", difference)
        intent.putExtra("study_name",study.title)
        startActivity(intent)
    }

    private fun studyDbListener():ListenerRegistration{
        val db = DatabaseManager().getDatabase().collection("events")
        val dbQuery = db.whereEqualTo("type","STUDY")
        val dbEvents: MutableList<Event> = mutableListOf()
        return dbQuery.addSnapshotListener{snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            for(docChange in snapshot!!.documentChanges){
                dbEvents.add(
                    Event(
                        title = docChange.document.getString("title")!!,
                        type = EventType.valueOf(docChange.document.getString("type")!!),
                        startTime = (docChange.document.get("start_time") as Timestamp).tolocalDateTime(),
                        endTime = (docChange.document.get("end_time") as Timestamp).tolocalDateTime(),
                        note = docChange.document.getString("note"),
                        eventId = docChange.document.getString("eventId")!!
                    )
                )
            }
            events = dbEvents.sortedBy{it.startTime}.toMutableList()
            viewAdapter.refreshList(events)
        }

    }

}

class SetStudyModeFragment: PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}


