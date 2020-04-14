package com.example.studentlifeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Subject

class CourseTabFragment : Fragment() {
    private lateinit var viewModel: SubjectsViewModel
    private lateinit var subjectRows: Pair<Subject, MutableList<TableRow>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_course_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run{
            ViewModelProviders.of(this).get(SubjectsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        viewModel.subjects.observe(this, Observer<MutableList<Subject>>{subjects ->
            setTable(subjects)
        })

    }

    private fun setTable(subjects:MutableList<Subject>){

    }


}
