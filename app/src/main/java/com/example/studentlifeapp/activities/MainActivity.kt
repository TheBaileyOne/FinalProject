package com.example.studentlifeapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.studentlifeapp.R
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.fragments.AddSubjectFragment
import com.example.studentlifeapp.fragments.SubjectsFragment
import com.example.studentlifeapp.pagers.MainPagerAdapter
import com.example.studentlifeapp.pagers.MainScreen
import com.example.studentlifeapp.pagers.getMainScreenForMenuItem
import com.example.studentlifeapp.util.putExtraJson
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


//TODO: Add a side navigation draw with access to user settings (Account managing)

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    SubjectsFragment.SubClickedListener, SubjectsFragment.SubAddClickedListener,AddSubjectFragment.OnSubjectSavedListener {

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
//        mainPagerAdapter.setItems(arrayListOf(MainScreen.DASHBOARD, MainScreen.TIMETABLE, MainScreen.SUBJECTS, MainScreen.STUDYMODE))
        mainPagerAdapter.setItems(arrayListOf(MainScreen.TIMETABLE, MainScreen.SUBJECTS, MainScreen.STUDYMODE))

        //show default screen
        val defaultScreen = MainScreen.TIMETABLE
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.option_logout-> {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,Login::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                return true}
        }
        return super.onOptionsItemSelected(item)
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
            putExtra("subRef", subject.getId())
        }
        startActivity(intent)
    }

    override fun subAddClick() {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AddSubjectFragment()
        fragment.setOnSubjectSavedListener(this)
        fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack("addSubFrag").commit()
//        val bottomNav = findViewById<View>(R.id.bottom_navigation_view)
//        bottomNav.visibility = View.GONE
        showBottomNav(false)
    }

    fun showBottomNav(show:Boolean){
        val bottomNav = findViewById<View>(R.id.bottom_navigation_view)
        if(show){
            bottomNav.visibility = View.VISIBLE
        }
        else{
            bottomNav.visibility = View.GONE
        }
    }

    override fun onSubjectSaved(subject: Subject) {
        showBottomNav(true)
        Log.d("onSubSaved","Listener called. \nSubject: ${subject.name}\nSubject Reference: ${subject.getId()}")
        val intent = Intent(this, SubjectDetails::class.java).apply{
            putExtraJson(subject)
            putExtra("subRef", subject.getId())
        }
        val fm = this.supportFragmentManager
        startActivity(intent)

        fm.popBackStack("addSubFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
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
