package com.example.studentlifeapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_subjects_tab.*
import kotlinx.android.synthetic.main.list_item.*

//class SubjectsAdapter(private var viewModel: SubjectsViewModel, val onClick: (Subject) ->Unit):
class SubjectsAdapter(private var subjects: MutableList<Subject> = mutableListOf(), val onClick: (Subject) ->Unit):
    RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>(){


    override fun getItemCount(): Int = subjects.size

    override fun onBindViewHolder(viewHolder: SubjectViewHolder, position: Int) {
        viewHolder.bind(subjects[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        return SubjectViewHolder(parent.inflate(R.layout.list_item))
    }

    //    inner class SubjectViewHolder(inflater : LayoutInflater,parent:ViewGroup):RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item,parent,false)){
    inner class SubjectViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),
        LayoutContainer {

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
    fun refreshList(newSubjects:MutableList<Subject>){
        subjects = newSubjects
        this.notifyDataSetChanged()
    }
}

class SubjectsTabFragment : Fragment() {

    //communicates to activity that a subject has been clicked
    interface SubClickedListener{
        fun subClicked(subject:Subject)
    }

    interface SubAddClickedListener{
        fun subAddClick()
    }

    private lateinit var subClickListener: SubClickedListener
    private lateinit var subAddClickListener: SubAddClickedListener
    private lateinit var viewModel: SubjectsViewModel
    private val subjectAdapter = SubjectsAdapter(mutableListOf()){ subject:Subject->subjectClicked(subject)}
//    private val subjectAdapter = SubjectsAdapter(mutableListOf<Subject>()){ subject:Subject->subjectClicked(subject)}
    lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    //ensure fragment actually attaches, and that activity implements interface
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SubClickedListener) {
            subClickListener = context
        } else {
            throw ClassCastException(
                "$context must implement SubClickListener"
            )

        }
        if (context is SubAddClickedListener) {
            subAddClickListener = context
        } else {
            throw ClassCastException(
                "$context must implement SubClickListener"
            )

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subjectAdapter.refreshList(mutableListOf())
    }

    fun onLogout(){
    }


    private fun subjectClicked(subject:Subject){
        subClickListener.subClicked(subject)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subjects_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subjects_recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        subjects_recyclerView.adapter=subjectAdapter
        subjects_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        subjectAdapter.notifyDataSetChanged()

        viewModel = activity?.run{
            ViewModelProviders.of(this).get(SubjectsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        viewModel.subjects.observe(viewLifecycleOwner, Observer<MutableList<Subject>>{subjects ->
            subjectAdapter.refreshList(subjects)
        })

        add_subject_button.setOnClickListener{
            subAddClickListener.subAddClick()
        }
    }



}
