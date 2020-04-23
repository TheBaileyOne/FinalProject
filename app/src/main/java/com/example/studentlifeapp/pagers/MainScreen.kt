package com.example.studentlifeapp.pagers

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.studentlifeapp.R
import com.example.studentlifeapp.fragments.*

// screens available for display in the main screen
enum class MainScreen(@IdRes val menuItemId: Int,
                      @DrawableRes val menuItemIconId: Int,
                      @StringRes val titleStringId: Int,
                      val fragment: Fragment
){
    DASHBOARD(R.id.bottom_navigation_item_dashboard, R.drawable.ic_dashboard,
        R.string.activity_main_bottom_screen_dashboard, DashboardFragment()),
    TIMETABLE(R.id.bottom_navigation_item_timeTable,R.drawable.ic_date_range,
        R.string.activity_main_bottom_screen_timeTable, TimetableFragment()),
    COURSE(R.id.bottom_navigation_item_subjects,R.drawable.ic_school,
        R.string.activity_main_bottom_screen_course, CourseFragment()),
    STUDYMODE(R.id.bottom_navigation_item_studyMode,R.drawable.ic_local_library,
        R.string.activity_main_bottom_screen_studyMode,StudyModeFragment()),
    MONEYMANAGER(R.id.bottom_navigation_item_moneyManager, R.drawable.ic_money,
        R.string.money_manager, MoneyManagerFragment())
}


fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId){
            return mainScreen
        }
    }
    return null
}