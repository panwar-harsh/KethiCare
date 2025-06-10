package com.example.greendoc

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class OnBoardingFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_on_boarding2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sliderView = view.findViewById<SliderComparisonView>(R.id.sliderView)
        val nextButton = view.findViewById<Button>(R.id.nextButton)

        try {
            val leftBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.unhealthy_image)
            val rightBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.healthy_image)

            if (leftBitmap == null || rightBitmap == null) {
                Log.e("OnBoardingFragment2", "One or both images are null")
                Toast.makeText(requireContext(), "Error: Images not found", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("OnBoardingFragment2", "Images loaded successfully")
                sliderView.setImages(leftBitmap, rightBitmap)
            }
        } catch (e: Exception) {
            Log.e("OnBoardingFragment2", "Error loading images: ${e.message}")
            Toast.makeText(requireContext(), "Error loading images", Toast.LENGTH_SHORT).show()
        }

        // Next button -> Navigate to OnBoardingFragment3
        nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment2_to_onBoardingFragment3)
        }
    }
}
