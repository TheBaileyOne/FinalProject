package com.example.studentlifeapp.pagers

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.studentlifeapp.R
import com.example.studentlifeapp.fragments.*

// screens available for display in the main screen
enum class MainScreen(
    @IdRes val menuItemId: Int,
    @StringRes val titleStringId: Int,
    val fragment: Fragment
){
    DASHBOARD(
        R.id.bottom_navigation_item_dashboard, R.string.activity_main_bottom_screen_dashboard,
        DashboardFragment()
    ),
    TIMETABLE(
        R.id.bottom_navigation_item_timeTable, R.string.activity_main_bottom_screen_timeTable,
        TimetableFragment()
    ),
    COURSE(
        R.id.bottom_navigation_item_subjects, R.string.activity_main_bottom_screen_course,
        CourseFragment()
    ),
    STUDYMODE(
        R.id.bottom_navigation_item_studyMode, R.string.activity_main_bottom_screen_studyMode,
        StudyModeFragment()
    ),
    MONEYMANAGER(
        R.id.bottom_navigation_item_moneyManager, R.string.money_manager,
        MoneyManagerFragment()
    )
}


fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId){
            return mainScreen
        }
    }
    return null
}