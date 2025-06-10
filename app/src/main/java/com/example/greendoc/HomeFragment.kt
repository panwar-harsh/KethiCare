package com.example.greendoc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.greendoc.Ecommerce.HomeScreenActivity
import com.example.greendoc.Ecommerce.OptionsActivity
import com.example.greendoc.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.plantIdentifierCardview.setOnClickListener {
            startActivity(Intent(requireContext(), PlantIdentifier::class.java))
        }

        binding.plantDiagnoseCardview.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivityDiagnose::class.java))
        }

        binding.plantWaterCalcuCardview.setOnClickListener {
            startActivity(Intent(requireContext(), WaterCalculatorActivity::class.java))
        }

        binding.plantLightMeterCardview.setOnClickListener {
            startActivity(Intent(requireContext(), LightMeterActivity::class.java))
        }

        binding.toolSellOrBuy.setOnClickListener {
            startActivity(Intent(context, OptionsActivity::class.java))
        }

        setupImageSlider()
    }

    private fun setupImageSlider() {
        val imageList = arrayListOf(
            SlideModel(R.drawable.slideshow_img1, ScaleTypes.FIT),
            SlideModel(R.drawable.slideshow_img2, ScaleTypes.FIT),
            SlideModel(R.drawable.slideshow_img3, ScaleTypes.FIT),
            SlideModel(R.drawable.slideshow_img4, ScaleTypes.FIT)
        )

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)

        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                // No action needed for double-click
            }

            override fun onItemSelected(position: Int) {
                when (position) {
                    1 -> startActivity(Intent(requireContext(), PlantIdentifier::class.java))
                    2 -> startActivity(Intent(requireContext(), CameraActivityDiagnose::class.java))
                    else -> Toast.makeText(requireContext(), "Selected Image $position", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
