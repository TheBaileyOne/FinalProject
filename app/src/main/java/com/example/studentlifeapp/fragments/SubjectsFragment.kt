package com.example.studentlifeapp.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.R
import com.example.studentlifeapp.activities.MainActivity
import com.example.studentlifeapp.addFragment
import com.example.studentlifeapp.data.importSubjects
import com.example.studentlifeapp.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_subjects.*
import kotlinx.android.synthetic.main.list_item.*
import java.lang.ClassCastException


class SubjectsAdapter( val subjects: MutableList<Subject> = mutableListOf(), val onClick: (Subject) ->Unit):RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>(){


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

    init{
        itemView.setOnClickListener {
            onClick(subjects[adapterPosition])
        }
    }
        fun bind(subject: Subject){
            list_title.text = subject.name
            list_description.text = subject.summary
        }
    }
}
class SubjectsFragment : Fragment() {

    //communicates to activity that a subject has been clicked
    interface SubClickedListener{
        fun subClicked(subject:Subject)
    }

    private lateinit var subClickListener: SubClickedListener
    private val subjects = importSubjects()
    private val subjectAdapter = SubjectsAdapter(subjects){subject:Subject->subjectClicked(subject)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    //ensure fragment actually attaches, and that activity implements interface
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is SubClickedListener){
            subClickListener = context
        } else{
            throw ClassCastException(
                "$context must implement SubClickListener"
            )

        }
    }
    private fun subjectClicked(subject:Subject){
        Toast.makeText(activity,"Clicked: ${subject.name}",Toast.LENGTH_LONG).show()
        subClickListener.subClicked(subject)

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
