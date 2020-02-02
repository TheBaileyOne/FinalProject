package com.example.studentlifeapp.fragments

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.data.importSubjects
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_subjects.*
import kotlinx.android.synthetic.main.list_item.*
import java.lang.ClassCastException


class SubjectsAdapter(private var subjects: MutableList<Subject> = mutableListOf(), val onClick: (Subject) ->Unit):RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>(){


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
    fun refreshList(newSubjects:MutableList<Subject>){
        subjects = newSubjects
        this.notifyDataSetChanged()
    }
}
class SubjectsFragment : Fragment() {

    //communicates to activity that a subject has been clicked
    interface SubClickedListener{
        fun subClicked(subject:Subject)
    }

    interface SubAddClickedListener{
        fun subAddClick()
    }

    private lateinit var subClickListener: SubClickedListener
    private lateinit var subAddClickListener: SubAddClickedListener
//    private val subjects = importSubjects()
    private var subjects = mutableListOf<Subject>()
    private lateinit var listener: ListenerRegistration
    private val subjectAdapter = SubjectsAdapter(subjects){subject:Subject->subjectClicked(subject)}
    lateinit var menuItem:MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        retainInstance = true
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
    private fun subjectClicked(subject:Subject){
        Toast.makeText(activity,"Clicked: ${subject.name}",Toast.LENGTH_LONG).show()
        subClickListener.subClicked(subject)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_add, menu)
//        menuItem = menu.findItem(R.id.action_add)
        super.onCreateOptionsMenu(menu, inflater)

    }

//    fun setMenuItemEnabled(enabled:Boolean){
//        menuItem.setEnabled(enabled)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add -> {
//                Toast.makeText(context, "add button selected",Toast.LENGTH_SHORT).show()
//                val fragmentManager = activity?.supportFragmentManager
//                val fragmentTransaction = fragmentManager?.beginTransaction()
//                val fragment = AddSubject()
////                fragment.setOnSubjectSavedListener(this)
//                fragmentTransaction?.replace(R.id.view_pager_container, fragment)?.addToBackStack(null)?.commit()
                subAddClickListener.subAddClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
        listener = subDbListener()

        subjects_recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        subjects_recyclerView.adapter=subjectAdapter
        subjects_recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
        subjectAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(context,"Fragment refreshed",Toast.LENGTH_SHORT).show()
//        TODO("Refresh the subject list")
    }

    private fun subDbListener(): ListenerRegistration {
        val db = DatabaseManager()
//        val dbSubjects:MutableList<Subject> = mutableListOf()
        return db.getDatabase().collection("subjects")
            .addSnapshotListener{snapshot, e ->
                if (e!= null){
                    Log.w(ContentValues.TAG, "snapshot listen failed.",e)
                    return@addSnapshotListener
                }
                for (docChange in snapshot!!.documentChanges){
                    val subject = Subject(
                            name = docChange.document.getString("name")!!,
                            summary = docChange.document.getString("summary")!!,
                            subjectStart = (docChange.document.get("subject_start")as Timestamp).tolocalDateTime(),
                            subjectEnd = (docChange.document.get("subject_end")as Timestamp).tolocalDateTime()
                        )
                    subject.setId(docChange.document.id)
//                    dbSubjects.add(subject)
                    subjects.add(subject)
                }
                subjectAdapter.refreshList(subjects)
            }
    }
}
