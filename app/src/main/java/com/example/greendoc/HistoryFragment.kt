package com.example.greendoc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greendoc.Adapters.PlantHistoryAdapter
import com.example.greendoc.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var plantAdapter: PlantHistoryAdapter
    private var firebaseListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadPlants()
    }

    private fun setupRecyclerView() {
        plantAdapter = PlantHistoryAdapter(emptyList()) { plant ->
            // Handle item click - navigate to appropriate detail screen
            navigateToPlantDetails(plant)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = plantAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
            setHasFixedSize(true)
        }
    }

    private fun navigateToPlantDetails(plant: Plant) {
        val intent = when (plant.type) {
            "diagnosed" -> Intent(requireContext(), DiagnosisResultActivity::class.java).apply {
                putExtra("plant_id", plant.id)
            }
            else -> Intent(requireContext(), PlantIdentifierResultActivity::class.java).apply {
                putExtra("plant_id", plant.id)
            }
        }
        startActivity(intent)
    }

    private fun loadPlants() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Remove any existing listener to avoid duplicates
        firebaseListener?.let {
            FirebaseDatabase.getInstance().reference
                .child("user_plants")
                .child(userId)
                .child("history")
                .removeEventListener(it)
        }

        firebaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plants = mutableListOf<Plant>()
                for (plantSnapshot in snapshot.children) {
                    val plant = plantSnapshot.getValue(Plant::class.java)
                    plant?.let {
                        it.id = plantSnapshot.key ?: ""
                        plants.add(it)
                    }
                }
                displayPlants(plants.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading plants: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("HistoryFragment", "Database error", error.toException())
            }
        }

        FirebaseDatabase.getInstance().reference
            .child("user_plants")
            .child(userId)
            .child("history")
            .addValueEventListener(firebaseListener!!)
    }

    private fun displayPlants(plants: List<Plant>) {
        if (plants.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            plantAdapter.updatePlants(plants)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up Firebase listener
        firebaseListener?.let {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@let
            FirebaseDatabase.getInstance().reference
                .child("user_plants")
                .child(userId)
                .child("history")
                .removeEventListener(it)
        }
        firebaseListener = null
        _binding = null
    }
}
