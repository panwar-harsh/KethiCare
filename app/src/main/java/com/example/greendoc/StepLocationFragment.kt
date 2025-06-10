package com.example.greendoc

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class StepLocationFragment : Fragment() {

    private lateinit var cardIndoor: CardView
    private lateinit var cardOutdoor: CardView
    private lateinit var CenterTitle: ImageView
    private lateinit var btnNext: Button

    private var selectedLocation: String = "Indoor" // default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_step_location, container, false)

        // Initialize views
        cardIndoor = view.findViewById(R.id.card_indoor)
        cardOutdoor = view.findViewById(R.id.card_outdoor)
        CenterTitle = view.findViewById(R.id.center_title)
        btnNext = view.findViewById(R.id.btn_next)

        // Default selection
        highlightSelectedCard(cardIndoor)
        removeHighlight(cardOutdoor)

        cardIndoor.setOnClickListener {
            selectedLocation = "Indoor"
            // Assuming CenterTitle is an ImageView
            CenterTitle.setImageResource(R.drawable.ic_indoor_text);
            highlightSelectedCard(cardIndoor)
            removeHighlight(cardOutdoor)
        }

        cardOutdoor.setOnClickListener {
            selectedLocation = "Outdoor"
            CenterTitle.setImageResource(R.drawable.ic_outdoor_text);
            highlightSelectedCard(cardOutdoor)
            removeHighlight(cardIndoor)
        }

        btnNext.setOnClickListener {
            // Send selectedLocation to parent activity or view model
            val activity = activity as? WaterStepsActivity
            activity?.plantLocation = selectedLocation

            // Move to next step
            activity?.goToNextStep()
            // You can store it in a shared ViewModel or bundle for next step
        }

        return view
    }

    private fun highlightSelectedCard(card: CardView) {
        val strokeDrawable = GradientDrawable()
        strokeDrawable.setStroke(6, ContextCompat.getColor(requireContext(), R.color.green)) // change to your green
        strokeDrawable.cornerRadius = 16f
        card.background = strokeDrawable
        card.setCardBackgroundColor(Color.WHITE)
    }

    private fun removeHighlight(card: CardView) {
        card.setCardBackgroundColor(Color.WHITE)
        card.background = null
    }
}
