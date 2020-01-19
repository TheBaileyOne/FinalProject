package com.example.studentlifeapp.fragments

import android.content.Intent
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
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventType
import com.example.studentlifeapp.data.importEvents
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.util.PrefUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_study_mode.*
import kotlinx.android.synthetic.main.fragment_study_mode.view.*
import kotlinx.android.synthetic.main.study_item.*
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
            study_item_title.text = study.title
            study_item_description.text= study.note
            val difference = ChronoUnit.MINUTES.between(study.startTime, study.endTime)
            study_item_time.text = "${Math.toIntExact(difference)} \nmins"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val events = importEvents()
        val studies = events.filter{
            it.type == EventType.STUDY
        }

        viewManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewAdapter = StudyAdapter(studies){study:Event ->studyClicked(study)}

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

}

class SetStudyModeFragment: PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}
