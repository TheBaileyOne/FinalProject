package com.example.studentlifeapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.importSubjects
import com.example.studentlifeapp.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_subjects.*
import kotlinx.android.synthetic.main.list_item.*

class SubjectsAdapter( val subjects: MutableList<Subject> = mutableListOf()):RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>(){


    override fun getItemCount(): Int = subjects.size

    override fun onBindViewHolder(viewHolder: SubjectViewHolder, position: Int) {
        viewHolder.bind(subjects[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        return SubjectViewHolder(inflater,parent)
        return SubjectViewHolder(parent.inflate(R.layout.list_item))
    }

//    inner class SubjectViewHolder(inflater : LayoutInflater,parent:ViewGroup):RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item,parent,false)){
    inner class SubjectViewHolder(override val containerView: View):RecyclerView.ViewHolder(containerView), LayoutContainer{


        fun bind(subject: Subject){
            list_title.text = subject.name
            list_description.text = subject.summary
        }
    }
}
class SubjectsFragment : Fragment() {

    private val subjects = importSubjects()
    private val subjectAdapter = SubjectsAdapter(subjects)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_subjects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        subjects_recyclerView.apply{
//            layoutManager = LinearLayoutManager(activity)
//            adapter = SubjectsAdapter()
//        }
        subjects_recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        subjects_recyclerView.adapter=subjectAdapter
        subjects_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        subjectAdapter.notifyDataSetChanged()
    }

}
