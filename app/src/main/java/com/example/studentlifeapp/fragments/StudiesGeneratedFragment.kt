package com.example.studentlifeapp.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Selection
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.SubjectDetails
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_item_view.*
import kotlinx.android.synthetic.main.fragment_studies_generated.*
import kotlinx.android.synthetic.main.fragment_study_mode.*
import org.threeten.bp.format.DateTimeFormatter
import java.lang.IllegalStateException


class StudiesGeneratedAdapter(var studies:MutableList<Event>): RecyclerView.Adapter<StudiesGeneratedAdapter.StudiesGeneratedViewHolder>(){
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private val formatter2= DateTimeFormatter.ofPattern("EEE\ndd\nMMM")
    private val newStudies = studies.toMutableList()

    init {
        setHasStableIds(true)
    }

//    fun setStudies(newStudies:List<Event>){
//        studies = newStudies
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudiesGeneratedViewHolder {
        return StudiesGeneratedViewHolder(parent.inflate(R.layout.event_item_view))
    }

    override fun onBindViewHolder(viewHolder: StudiesGeneratedViewHolder, position: Int) {
        val study = studies[position]
        tracker?.let{
            viewHolder.bind(study, it.isSelected(position.toLong()))
        }
//        viewHolder.bind(studies[position])

    }

    override fun getItemCount(): Int = studies.size

    override fun getItemId(position: Int): Long = position.toLong()

    private var tracker: SelectionTracker<Long>? = null
    fun setTracker(tracker:SelectionTracker<Long>?){
        this.tracker = tracker
    }

    fun removeStudy(study:Event){
        studies.remove(study)
        notifyDataSetChanged()
    }
    inner class StudiesGeneratedViewHolder(override val containerView:View):RecyclerView.ViewHolder(containerView), LayoutContainer{

        fun bind(study:Event, isActivated: Boolean = false){
            itemView.isActivated = isActivated
            event_view_title.text = study.title
            event_view_icon.visibility = View.INVISIBLE
            if (study.location?.basicDisplay() != null){
                event_view_location.text = study.location?.basicDisplay()
                event_view_location_icon.visibility = View.VISIBLE
            }else{
                event_view_location.text = ""
                event_view_location_icon.visibility = View.INVISIBLE
            }

            if (isActivated && newStudies.contains(study)){
//                newStudies.remove(study)
                event_view_background.background = ColorDrawable(Color.LTGRAY)
            }
            else if (!isActivated && !newStudies.contains(study)){
//                newStudies.add(study)
                event_view_background.background = ColorDrawable(Color.WHITE)
            }
            else{
                event_view_background.background = ColorDrawable(Color.WHITE)
            }
            event_view_time.text = "${formatter.format(study.startTime)}\n-\n${formatter.format(study.endTime)}"
            event_view_icon_text.visibility = View.VISIBLE
            event_view_icon_text.text = formatter2.format(study.startTime)
        }
        fun getItemDetails():ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>(){
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
                fun getObject(): Event{
                    return studies[adapterPosition]
                }
            }
    }
//    fun getNewStudies() = newStudies
//    fun getStudy(key:Long)=
}

class StudiesGeneratedFragment(private var studies:MutableList<Event>) : Fragment() {

//    internal lateinit var callback: AddEventFragment.OnEventSavedListener
//
//    fun setOnEventSavedListener(callback: AddEventFragment.OnEventSavedListener){
//        this.callback = callback
//    }
    var tracker: SelectionTracker<Long>? = null
    private val studyAdapter = StudiesGeneratedAdapter(studies)
    private lateinit var selectedStudy: MutableList<Event>

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) tracker?.onSaveInstanceState(outState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_studies_generated, container, false)
        if (savedInstanceState!= null) tracker?.onRestoreInstanceState(savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studies_generated_recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        studies_generated_recyclerView.adapter = studyAdapter
        studies_generated_recyclerView.addItemDecoration((DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)))
        studyAdapter.notifyDataSetChanged()
        Log.d("Generated Studies","Total Generated Studies: ${studies.size}")
        trackSelectedItems()

        studies_generated_remove.setOnClickListener{

            if (::selectedStudy.isInitialized && selectedStudy.size > 0){

                for(study in selectedStudy){
                    tracker?.clearSelection()
                    studyAdapter.removeStudy(study)
                }
                if (studies.size <1){
                    studies_generated_save.text = "Cancel"
                }
                selectedStudy.clear()

            }
            else{
                Toast.makeText(context, "No events selected for deletion", Toast.LENGTH_LONG).show()
            }
            studies_generated_remove.text = "Remove Selected"

        }
        studies_generated_save.setOnClickListener{
            if (studies.isEmpty()){
                Toast.makeText(context, "No Study Events generated", Toast.LENGTH_LONG).show()
            }
            else{
                (activity as SubjectDetails).addStudies(studies)
            }
            this.activity?.onBackPressed()
        }

    }

    private fun trackSelectedItems(){
        tracker = SelectionTracker.Builder<Long>("selection-1",
            studies_generated_recyclerView,
            ItemIdKeyProvider(studies_generated_recyclerView),
            ItemLookup(studies_generated_recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>(){
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = tracker?.selection!!.size()
                    if(items>0){
                        studies_generated_remove.text = "Remove Selected ($items)"
                        selectedStudy = tracker?.selection!!.map{
                            studyAdapter.studies[it.toInt()]
                        }.toMutableList()
                        Log.d("Selected Studies", "Number = ${selectedStudy.size}")}

                }
            }
        )
        studyAdapter.setTracker(tracker)
    }


    inner class ItemIdKeyProvider(private val recyclerView:RecyclerView)
        :ItemKeyProvider<Long>(SCOPE_MAPPED){
        override fun getKey(position: Int): Long? {
            return recyclerView.adapter?.getItemId(position)
                ?: throw IllegalStateException("RecyclerView adapter is not set!")

        }

        override fun getPosition(key: Long): Int {
            val viewHolder = recyclerView.findViewHolderForItemId(key)
            return viewHolder?.layoutPosition ?:RecyclerView.NO_POSITION
        }

    }
    inner class ItemLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<Long>(){
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if(view != null){
                return(recyclerView.getChildViewHolder(view) as StudiesGeneratedAdapter.StudiesGeneratedViewHolder)
                    .getItemDetails()
            }
            return null
        }
    }

}