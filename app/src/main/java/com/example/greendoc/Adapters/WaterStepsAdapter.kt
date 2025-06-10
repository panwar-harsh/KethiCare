package com.example.greendoc.Adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.greendoc.StepHumidityFragment
import com.example.greendoc.StepLocationFragment
import com.example.greendoc.StepTemperatureFragment
import com.example.greendoc.watercalculator.StepPotVolumeFragment

class WaterStepsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4 // 4 steps

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StepLocationFragment()
            1 -> StepHumidityFragment()
            2 -> StepTemperatureFragment()
            3 -> StepPotVolumeFragment()
            else -> throw IllegalArgumentException("Invalid step position")
        }
    }
}
