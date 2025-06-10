package com.example.greendoc.Documentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.greendoc.R
import com.example.greendoc.databinding.FragmentDocumentsBinding

class DocumentsFragment : Fragment() {
    private lateinit var binding: FragmentDocumentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for each category
        binding.categoryRice.setOnClickListener { launchActivity(RiceActivity::class.java) }
        binding.categoryMango.setOnClickListener { launchActivity(MangoActivity::class.java) }
        binding.categoryRagi.setOnClickListener { launchActivity(RagiActivity::class.java) }
        binding.categoryTurmericGinger.setOnClickListener { launchActivity(TurmericGingerActivity::class.java) }
        binding.categoryCinnamon.setOnClickListener { launchActivity(CinnamonActivity::class.java) }
        binding.categoryBetelNut.setOnClickListener { launchActivity(BetelNutActivity::class.java) }
        binding.categoryNute.setOnClickListener { launchActivity(NuteActivity::class.java) }
        binding.categoryChiku.setOnClickListener { launchActivity(ChikuActivity::class.java) }
        binding.categoryCoconut.setOnClickListener { launchActivity(CoconutActivity::class.java) }
        binding.categoryPepper.setOnClickListener { launchActivity(PepperActivity::class.java) }
        binding.categoryMogra.setOnClickListener { launchActivity(MograActivity::class.java) }
        binding.categoryVegetable.setOnClickListener { launchActivity(VegetableActivity::class.java) }
    }

    private fun launchActivity(activityClass: Class<*>) {
        startActivity(Intent(requireContext(), activityClass))
    }
}