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
import com.example.studentlifeapp.data.Event
import com.example.studentlifeapp.data.EventsParser
import com.example.studentlifeapp.data.Subject
import com.example.studentlifeapp.data.Transaction
import com.example.studentlifeapp.fragments.*
import com.example.studentlifeapp.pagers.MainPagerAdapter
import com.example.studentlifeapp.pagers.MainScreen
import com.example.studentlifeapp.pagers.getMainScreenForMenuItem
import com.example.studentlifeapp.util.Utils
import com.example.studentlifeapp.util.putExtraJson
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

//Inherits listener from all associated fragments, for passing data and triggering methods
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    SubjectsTabFragment.SubClickedListener, SubjectsTabFragment.SubAddClickedListener,
    AddSubjectFragment.OnSubjectSavedListener, Utils.EventDetailClickListener,
    EventDetailsFragment.EventEditListener, AddEventFragment.OnEventSavedListener,
    MoneyTabFragment.TransactionAddClickListener, MoneyTabFragment.TransactionClickedListener {


    private lateinit var viewPager: ViewPager
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mainPagerAdapter:MainPagerAdapter
    private var events = mutableListOf<Event>()
    private var navShowing = true

    /**
     * Method overrides back press to ensure correct page title is showing
     * Also ensures bottom navigation is showing when returning to fragments in ViewPager
     */
    override fun onBackPressed() {
        super.onBackPressed()
        if(!navShowing)showBottomNav(true)
        supportActionBar?.show()
        val title = mainPagerAdapter.getItems()[viewPager.currentItem].titleStringId
        supportActionBar?.setTitle(title)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        //Set up the view pager and congigure adapter
        viewPager=findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 4
        bottomNavigationView=findViewById(R.id.bottom_navigation_view)
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager)

        //set items to be displayed
        mainPagerAdapter.setItems(arrayListOf(MainScreen.DASHBOARD, MainScreen.TIMETABLE, MainScreen.COURSE,
            MainScreen.STUDYMODE,MainScreen.MONEYMANAGER))

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

    /**
     * Inflate the logout menu, which displays the logout, about, and password changing options
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    /**
     * Perform actions based on the options item selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.option_logout-> {
                //Sign out user and finish all activities and stacks
                FirebaseAuth.getInstance().signOut()
                viewModelStore.clear()
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                return true
            }
            R.id.option_about_app ->{
                //Open AboutAppFragment()
                val fragmentManager = this.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment = AboutAppFragment()
                fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack(null).commit()
                showBottomNav(false)
                return true
            }
            R.id.option_change_password->{
                //Open AccountManagementFragment() for changing password
                val fragmentManager = this.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment = AccountManagementFragment()
                fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack(null).commit()
                showBottomNav(false)
                return true
            }
            android.R.id.home -> {
                //Back to the previous activity/fragment in stack
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * scrolls ViewPager to show the selected screen
     */
    private fun scrollToScreen(mainScreen:MainScreen){
        val screenPosition = mainPagerAdapter.getItems().indexOf(mainScreen)
        if(screenPosition != viewPager.currentItem){
            viewPager.currentItem = screenPosition

        }
    }

    /**
     *  Overrides method from interface
     *  When subject clicked in SubjectsTabFragment(), SubjectDetails() activity is opened with subject passed
     */
    override fun subClicked(subject: Subject) {
        val intent = Intent(this, SubjectDetails::class.java).apply{
            putExtraJson("subject",subject)
            putExtra("subRef", subject.getId())
            putExtraJson("events",
                EventsParser(events)
            )
        }
        startActivity(intent)
    }

    /**
     *  Overrides method from interface
     *  When Add Subject Button clicked in SubjectsTabFragment(), AddSubjectFragment Opened
     */
    override fun subAddClick() {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AddSubjectFragment()
        fragment.setOnSubjectSavedListener(this)
        fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack("addSubFrag").commit()
        showBottomNav(false)
    }


    /**
     * Toggle bottom navigation menu visibility
     */
    fun showBottomNav(show:Boolean){
        val bottomNav = findViewById<View>(R.id.bottom_navigation_view)
        if(show){
            bottomNav.visibility = View.VISIBLE
            navShowing=true
        }
        else{
            bottomNav.visibility = View.GONE
            navShowing=false
        }
    }

    /**
     *  Overrides method from interface
     *  When subject saved in AddSubjectFragment(), SubjectDetails() is opened with the created subjects as extra
     */
    override fun onSubjectSaved(subject: Subject) {
        showBottomNav(true)
        Log.d("onSubSaved","Listener called. \nSubject: ${subject.name}\nSubject Reference: ${subject.getId()}")
        val intent = Intent(this, SubjectDetails::class.java).apply{
            putExtraJson("subject",subject)
            putExtra("subRef", subject.getId())
        }
        val fm = this.supportFragmentManager
        startActivity(intent)
        fm.popBackStack("addSubFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    /**
     * Set the selected menu item
     */
    private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int){
        bottomNavigationView.setOnNavigationItemSelectedListener(null)
        bottomNavigationView.selectedItemId=menuItemId
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    /**
     * Override function
     * On navigation item click scroll to the related screen
     */
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        getMainScreenForMenuItem(menuItem.itemId)?.let{
            scrollToScreen(it)
            supportActionBar?.setTitle(it.titleStringId)
            return true
        }
        return false
    }

    /**
     *  Overrides method from interface
     *  When event item clicked, EventDetailsFragment() opened with clicked event
     */
    override fun onEventClicked(tag: String, event: Event) {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EventDetailsFragment(event)
        fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack("eventDetailsFrag").commit()
        showBottomNav(false)
    }

    /**
     *  Overrides method from interface
     *  When Edit Event button clicked, AddEventFragment() opened, with event item to prefill for editing
     */
    override fun eventEditClicked(event: Event) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AddEventFragment(null, event)
        fragment.setOnEventSavedListener(this)
        fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack(null).commit()
        showBottomNav(false)
    }

    override fun onEventSaved(events: MutableList<Event>) {
        TODO("not implemented")
    }

    /**
     *  Overrides method from interface
     *  When Add Transaction Button clicked, AddTransactionFragment() opened
     */
    override fun transactionAddClick() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AddTransactionFragment()
        fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack(null).commit()
        showBottomNav(false)
    }

    /**
     *  Overrides method from interface
     *  When transaction is clicked, TransactionDetailsFragment() opened
     */
    override fun transactionClicked(transaction: Transaction) {
        val fragmentManager = this.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = TransactionDetailsFragment(transaction)
        fragmentTransaction.replace(R.id.view_pager_container, fragment).addToBackStack("transactionDetailFrag").commit()
        showBottomNav(false)
    }
}


