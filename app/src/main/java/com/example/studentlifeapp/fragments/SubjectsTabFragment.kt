package com.example.studentlifeapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_course.*
import kotlinx.android.synthetic.main.fragment_subjects_tab.*
import kotlinx.android.synthetic.main.list_item.*
import java.lang.ClassCastException

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
    private val subjectAdapter = SubjectsAdapter(mutableListOf<Subject>()){ subject:Subject->subjectClicked(subject)}
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

    fun onLogout(){
//        subjects.clear()
    }


    private fun subjectClicked(subject:Subject){
        Toast.makeText(activity,"Clicked: ${subject.name}", Toast.LENGTH_LONG).show()
        subClickListener.subClicked(subject)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add -> {
                subAddClickListener.subAddClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
        viewModel.subjects.observe(this, Observer<MutableList<Subject>>{subjects ->
            subjectAdapter.refreshList(subjects)
        })

        add_subject_button.setOnClickListener{
            subAddClickListener.subAddClick()
        }
    }



}
