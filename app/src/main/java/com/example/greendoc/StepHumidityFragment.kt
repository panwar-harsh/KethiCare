package com.example.greendoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class StepHumidityFragment : Fragment() {

    private val viewModel: WaterCalculatorViewModel by activityViewModels()

    private lateinit var tvHumidityValue: TextView
    private lateinit var tvHumidityLabel: TextView
    private lateinit var seekBarHumidity: SeekBar
    private lateinit var btnNext: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_step_humidity, container, false)

        tvHumidityValue = view.findViewById(R.id.tvHumidityValue)
        tvHumidityLabel = view.findViewById(R.id.textView14)
        seekBarHumidity = view.findViewById(R.id.seekBarHumidity)
        btnNext = view.findViewById(R.id.btn_next)

        setupSeekBar()
        setupNextButton()

        return view
    }

    private fun setupSeekBar() {
        updateHumidityText(seekBarHumidity.progress)

        seekBarHumidity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvHumidityValue.text = "$progress%"
                updateHumidityText(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateHumidityText(progress: Int) {
        val label = when (progress) {
            in 0..20 -> "Very Dry Environment"
            in 21..40 -> "Dry Environment"
            in 41..60 -> "Moderate Environment"
            in 61..80 -> "Humid Environment"
            else -> "Very Humid Environment"
        }
        tvHumidityLabel.text = label
    }

    private fun setupNextButton() {
        btnNext.setOnClickListener {
            val humidity = seekBarHumidity.progress
            viewModel.setHumidity(humidity)
            val activity = activity as? WaterStepsActivity
            activity?.goToNextStep()
        }
    }
}
