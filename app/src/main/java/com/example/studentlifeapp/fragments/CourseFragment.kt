package com.example.studentlifeapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.DatabaseManager
import com.example.studentlifeapp.inflate
import com.example.studentlifeapp.tolocalDateTime
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_course.*
import kotlinx.android.synthetic.main.list_item.*
import java.lang.ClassCastException

class CoursePagerAdapter(fragment: Fragment):FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            1 -> CourseTabFragment()
            else -> SubjectsTabFragment()
        }
    }


}


class CourseFragment : Fragment() {
    private lateinit var courseAdapter: CoursePagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_course, container, false)
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



    }

}
