package com.example.greendoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.greendoc.databinding.FragmentStepTemperatureBinding

class StepTemperatureFragment : Fragment() {

    private var _binding: FragmentStepTemperatureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WaterCalculatorViewModel by activityViewModels()

    private var isCelsiusSelected = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTemperatureBinding.inflate(inflater, container, false)

        setupUnitToggle()
        setupSeekBar()
        setupNextButton()

        return binding.root
    }

    private fun setupUnitToggle() {
        updateCardSelection()

        binding.linearLayout.getChildAt(0).setOnClickListener {
            isCelsiusSelected = true
            updateCardSelection()
            updateTemperatureDisplay(binding.seekBarTemperature.progress)
        }

        binding.linearLayout.getChildAt(1).setOnClickListener {
            isCelsiusSelected = false
            updateCardSelection()
            updateTemperatureDisplay(binding.seekBarTemperature.progress)
        }
    }

    private fun updateCardSelection() {
        val blackColor = ContextCompat.getColor(requireContext(), R.color.black)
        val greenColor = ContextCompat.getColor(requireContext(), R.color.green)

        val cardC = binding.linearLayout.getChildAt(0) as CardView
        val cardF = binding.linearLayout.getChildAt(1) as CardView

        cardC.setCardBackgroundColor(if (isCelsiusSelected) blackColor else greenColor)
        cardF.setCardBackgroundColor(if (isCelsiusSelected) greenColor else blackColor)
    }

    private fun setupSeekBar() {
        binding.seekBarTemperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTemperatureDisplay(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        updateTemperatureDisplay(binding.seekBarTemperature.progress)
    }

    private fun updateTemperatureDisplay(progress: Int) {
        val temperature = if (isCelsiusSelected) {
            "$progress°C"
        } else {
            val fahrenheit = (progress * 9 / 5) + 32
            "$fahrenheit°F"
        }
        binding.tvTemperatureValue.text = temperature
    }

    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            val temperature = binding.seekBarTemperature.progress
            viewModel.setTemperature(temperature)  // Always store in °C
            (activity as? WaterStepsActivity)?.goToNextStep()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
