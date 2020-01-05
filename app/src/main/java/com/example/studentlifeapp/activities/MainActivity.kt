package com.example.studentlifeapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.viewpager.widget.ViewPager
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.fragments.SubjectsFragment
import com.example.studentlifeapp.pagers.MainPagerAdapter
import com.example.studentlifeapp.pagers.MainScreen
import com.example.studentlifeapp.pagers.getMainScreenForMenuItem
import com.example.studentlifeapp.putExtraJson
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, SubjectsFragment.SubClickedListener {

    private lateinit var viewPager: ViewPager
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mainPagerAdapter:MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO: Login/authentication
        //TODO: get events data
        //initialize views
        viewPager=findViewById(R.id.view_pager)
        bottomNavigationView=findViewById(R.id.bottom_navigation_view)
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager)

        //set items to be displayed
        mainPagerAdapter.setItems(arrayListOf(MainScreen.DASHBOARD, MainScreen.TIMETABLE, MainScreen.SUBJECTS, MainScreen.STUDYMODE))

        //show default screen
        val defaultScreen = MainScreen.DASHBOARD
        scrollToScreen(defaultScreen)
        selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)
        supportActionBar?.setTitle(defaultScreen.titleStringId)

        //Set the listener for item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        //Attach adapter to view pager to make it select bottom navigation
        //change to right values when selected
        viewPager.adapter = mainPagerAdapter
        viewPager.addOnPageChangeListener(object:ViewPager.SimpleOnPageChangeListener(){
            override fun onPageSelected(position: Int) {
                    val selectedScreen = mainPagerAdapter.getItems() [position]
                    selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
                    supportActionBar?.setTitle(selectedScreen.titleStringId)

            }
        })
    }

    //scrolls ViewPager to show the screen
    private fun scrollToScreen(mainScreen:MainScreen){
        val screenPosition = mainPagerAdapter.getItems().indexOf(mainScreen)
        if(screenPosition != viewPager.currentItem){
            viewPager.currentItem = screenPosition
            if(mainScreen == MainScreen.SUBJECTS){

            }
        }
    }

    //from interface, when subject is clicked listener
    override fun subClicked(subject: Subject) {
        val intent = Intent(this, SubjectDetails::class.java).apply{
            putExtraJson(subject)
        }
        startActivity(intent)
    }

    //select item in bottom navigation
    private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int){
        bottomNavigationView.setOnNavigationItemSelectedListener(null)
        bottomNavigationView.selectedItemId=menuItemId
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    //listener for registering navigation clicks
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        getMainScreenForMenuItem(menuItem.itemId)?.let{
            scrollToScreen(it)
            supportActionBar?.setTitle(it.titleStringId)
            return true
        }
        return false
    }


}
