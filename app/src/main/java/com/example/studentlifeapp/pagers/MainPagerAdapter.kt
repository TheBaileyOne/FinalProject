package com.example.studentlifeapp.pagers

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    private val screens = arrayListOf<MainScreen>()

    fun setItems(screens: List<MainScreen>){
        this.screens.apply{
            clear()
            addAll(screens)
            notifyDataSetChanged()
        }
    }

    fun getItems(): List<MainScreen>{
        return screens
    }

    override fun getItem(position: Int): Fragment {
        return screens[position].fragment
    }

    override fun getCount(): Int {
        return screens.size
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        super.restoreState(state, loader)
        try{
            super.restoreState(state, loader)
        }catch(e:Exception){
            Log.e("TAG", "error Restore state of fragment: ${e.message}", e)

        }
    }
}