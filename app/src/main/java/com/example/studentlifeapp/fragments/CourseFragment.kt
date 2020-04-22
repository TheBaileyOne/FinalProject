package com.example.studentlifeapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.AcademicYear
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_course.*
import kotlinx.android.synthetic.main.list_item.*
import java.lang.ClassCastException
import java.lang.Exception

class CoursePagerAdapter(fragment: Fragment):FragmentStateAdapter(fragment){
    val fragments:MutableList<Fragment> = mutableListOf()
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment =  when(position){
            1 -> CourseTabFragment()
            else -> SubjectsTabFragment()
        }
        fragments.add(fragment)
        return fragment
    }


}


class CourseFragment : Fragment() {
    private lateinit var courseAdapter: CoursePagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var courseTabFragment:CourseTabFragment
    private lateinit var subjectsTabFragment :SubjectsTabFragment
    private lateinit var listener:ListenerRegistration
    private var subjects = mutableListOf<Subject>()
    private lateinit var viewModel: SubjectsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_course, container, false)
//        viewModel.setSubjects(subjects)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(SubjectsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        viewModel.setSubjects(subjects)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        subjects.clear()
        viewModel.setSubjects(mutableListOf())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout:TabLayout = view.findViewById(R.id.course_tabs)
        courseAdapter = CoursePagerAdapter(this)
        viewPager = view.findViewById(R.id.course_view_pager)
        viewPager.adapter = courseAdapter
        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            tab.text = when (position) {
                0 -> "Subjects"
                1 -> "Course Grades"
                else -> "Error"
            }
        }.attach()
        listener = subDbListener()

    }
    private fun subDbListener(): ListenerRegistration {
        val db = DatabaseManager()
        return db.getDatabase().collection("subjects")
            .addSnapshotListener{snapshot, e ->
                if (e!= null){
                    return@addSnapshotListener
                }
                for (docChange in snapshot!!.documentChanges){
                    val subject = Subject(
                        name = docChange.document.getString("name")!!,
                        summary = docChange.document.getString("summary")!!,
                        subjectStart = (docChange.document.get("subject_start")as Timestamp).tolocalDateTime(),
                        subjectEnd = (docChange.document.get("subject_end")as Timestamp).tolocalDateTime(),
                        percentage = if (docChange.document.getDouble("percentage")!=null) docChange.document.getDouble("percentage")!! else 0.0,
                        credits = if(docChange.document.getDouble("credits")!=null) docChange.document.getDouble("credits")!!.toInt() else 20,
                        academicYear = if(docChange.document.getString("academic_year")!=null) AcademicYear.valueOf(docChange.document.getString("academic_year")!!)
                                        else AcademicYear.FIRST_YEAR
                    )
                    subject.setId(docChange.document.id)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            subjects.add(subject)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val subToReplace = subjects.indexOf(subjects.find { it.getId() ==subject.getId()})
                            subjects[subToReplace] = subject
                        }
                        else -> {
                            val subToReplace = subjects.indexOf(subjects.find { it.getId() ==subject.getId()})
                            subjects.removeAt(subToReplace)
                        }
                    }
                }
                viewModel.setSubjects(subjects.toMutableList())

            }
    }

}
class SubjectsViewModel : ViewModel(){
    val subjects: MutableLiveData<MutableList<Subject>> = MutableLiveData()
    fun getSubjects() = subjects.value
    fun setSubjects(subjects:MutableList<Subject>){
        this.subjects.value = subjects
    }
}
