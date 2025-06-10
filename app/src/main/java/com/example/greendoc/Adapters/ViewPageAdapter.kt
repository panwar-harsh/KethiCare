package com.example.greendoc.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.greendoc.GardenFragment
import com.example.greendoc.ReminderFragment
import com.example.greendoc.HistoryFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        GardenFragment(),
        ReminderFragment(),
        HistoryFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
