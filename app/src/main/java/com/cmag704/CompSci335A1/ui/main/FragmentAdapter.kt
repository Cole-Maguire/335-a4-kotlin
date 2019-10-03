package com.cmag704.CompSci335A1.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> HomeFragment.newInstance()
        1 -> NewsFragment.newInstance()
        2 -> DisplaysFragment.newInstance()
        3-> CommentsFragment.newInstance()
        4-> ShopFragment.newInstance()
        else -> null
    }
    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Home"
        1 -> "News"
        2 -> "Displays"
        3->"Comments"
        4->"Shop"
        else -> ""
    }

    override fun getCount(): Int = 5
}