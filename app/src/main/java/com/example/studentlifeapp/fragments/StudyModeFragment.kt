package com.example.studentlifeapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.StudyMode
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.util.PrefUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_study_mode.*
import kotlinx.android.synthetic.main.fragment_study_mode.view.*
import kotlinx.android.synthetic.main.study_item.*
import org.threeten.bp.LocalDateTime
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
    private lateinit var eventViewModel:EventsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_study_mode, container, false)

        eventViewModel = activity?.run{
            ViewModelProviders.of(this).get(EventsViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        view.studyButtonManual.setOnClickListener{
            Log.d("studyButton","Selected")
            val intent = Intent(context, StudyMode::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }

        return view

    }
    //TODO: Sort out list reloading, and make list fit in screen better

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (PrefUtil.getTimerState(requireContext())){
            StudyMode.TimerState.RUNNING ->{
                studyButtonManual.text = getString(R.string.view_study_timer)
                fragment.visibility = View.GONE
                study_recyclerView.visibility = View.GONE
                textView13.text = getString(R.string.study_running)
            }
            StudyMode.TimerState.PAUSED -> {
                studyButtonManual.text = getString(R.string.view_study_timer)
                fragment.visibility = View.GONE
                study_recyclerView.visibility = View.GONE
                textView13.text = getString(R.string.study_running)
            }
            else -> {
                fragment.visibility = View.VISIBLE
                study_recyclerView.visibility = View.VISIBLE
                textView13.text = getString(R.string.select_study)
                studyButtonManual.text = getString(R.string.start_custom_study)

            }
        }
        eventViewModel.events.observe(viewLifecycleOwner, Observer { eventsModel ->
            events = eventsModel.filter{ it.type == EventType.STUDY && (it.startTime.isAfter(LocalDateTime.now()) || it.startTime.isEqual(LocalDateTime.now()))}.toMutableList()
            events.sortBy { it.startTime }
            viewAdapter.refreshList(events)
        })
        viewManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewAdapter = StudyAdapter(events){study:Event ->studyClicked(study)}

        recyclerView = study_recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(DividerItemDecoration(context,RecyclerView.VERTICAL))
        viewAdapter.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()
        when (PrefUtil.getTimerState(requireContext())){
            StudyMode.TimerState.RUNNING ->{
                studyButtonManual.text = getString(R.string.view_study_timer)
                fragment.visibility = View.GONE
                study_recyclerView.visibility = View.GONE
                textView13.text = getString(R.string.study_running)
            }
            StudyMode.TimerState.PAUSED -> {
                studyButtonManual.text = getString(R.string.view_study_timer)
                fragment.visibility = View.GONE
                study_recyclerView.visibility = View.GONE
                textView13.text = getString(R.string.study_running)
            }
            else -> {
                fragment.visibility = View.VISIBLE
                study_recyclerView.visibility = View.VISIBLE
                textView13.text = getString(R.string.select_study)
                studyButtonManual.text = getString(R.string.start_custom_study)
            }
        }
    }

    private fun studyClicked(study:Event){
        val intent = Intent(context, StudyMode::class.java)
        val difference = ChronoUnit.MINUTES.between(study.startTime, study.endTime).toInt()
        intent.putExtra("timer_length", difference)
        intent.putExtra("study_name",study.title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}

class SetStudyModeFragment: PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}


