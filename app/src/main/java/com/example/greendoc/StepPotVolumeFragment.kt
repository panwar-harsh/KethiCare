package com.example.greendoc.watercalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.greendoc.R
import com.example.greendoc.WaterStepsActivity
import com.example.greendoc.databinding.FragmentStepPotVolumeBinding

class StepPotVolumeFragment : Fragment() {

    private var _binding: FragmentStepPotVolumeBinding? = null
    private val binding get() = _binding!!

    private var isLiters = true // default unit
    private var volume = 10 // default value

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepPotVolumeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUnitSwitching()
        setupSeekBar()
        setupNextButton()
    }

    private fun setupSeekBar() {
        binding.seekBarVolume.max = 20
        binding.seekBarVolume.progress = volume
        updateVolumeText(volume)

        binding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                volume = value
                updateVolumeText(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupUnitSwitching() {
        binding.cardLiters.setOnClickListener {
            isLiters = true
            updateVolumeText(volume)
            highlightSelectedUnit(binding.cardLiters, binding.cardGallons)
        }

        binding.cardGallons.setOnClickListener {
            isLiters = false
            updateVolumeText(volume)
            highlightSelectedUnit(binding.cardGallons, binding.cardLiters)
        }

        // Set default selected
        highlightSelectedUnit(binding.cardLiters, binding.cardGallons)
    }

    private fun updateVolumeText(value: Int) {
        val displayValue = if (isLiters) {
            "$value L"
        } else {
            val gallons = (value * 0.264172).toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP)
            "$gallons GAL"
        }
        binding.tvVolumeValue.text = displayValue
    }

    private fun highlightSelectedUnit(selected: CardView, unselected: CardView) {
        selected.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
        unselected.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            val activity = activity as? WaterStepsActivity
            val finalVolume = if (isLiters) volume.toDouble() else volume * 0.264172
            activity?.setPotVolume(finalVolume, if (isLiters) "L" else "GAL")
            activity?.goToNextStep()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
