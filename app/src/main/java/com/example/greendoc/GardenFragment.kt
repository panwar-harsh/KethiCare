package com.example.greendoc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greendoc.Plant
import com.example.greendoc.Adapters.PlantAdapter
import com.example.greendoc.databinding.FragmentGardenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GardenFragment : Fragment() {
    private var _binding: FragmentGardenBinding? = null
    // Safe binding access that checks for fragment view lifecycle
    private val binding get() = _binding ?: throw IllegalStateException("Cannot access binding after onDestroyView or before onCreateView")

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var plantAdapter: PlantAdapter
    private var firebaseListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGardenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupFirebaseListener()
    }

    private fun setupRecyclerView() {
        plantAdapter = PlantAdapter(emptyList()) { plant ->
            showPlantDetails(plant)
        }
        binding.rvPlants.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = plantAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnAddPlants.setOnClickListener {
            startActivity(Intent(requireContext(), PlantIdentifier::class.java))
        }
    }

    fun setupFirebaseListener() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Remove any existing listener to avoid duplicates
        firebaseListener?.let {
            database.child("user_plants").child(userId).child("garden").removeEventListener(it)
        }

        firebaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plants = mutableListOf<Plant>()
                for (plantSnapshot in snapshot.children) {
                    val plant = plantSnapshot.getValue(Plant::class.java)
                    plant?.let {
                        if (!it.deleted) {
                            it.id = plantSnapshot.key ?: ""
                            plants.add(it)
                        }
                    }
                }

                try {
                    if (plants.isNotEmpty()) {
                        showPlantsList(plants)
                    } else {
                        showEmptyState()
                    }
                } catch (e: IllegalStateException) {
                    Log.e("GardenFragment", "Fragment view not available", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading plants: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        database.child("user_plants").child(userId).child("garden").addValueEventListener(firebaseListener!!)
    }

    private fun showPlantsList(plants: List<Plant>) {
        try {
            binding.apply {
                emptyState.visibility = View.GONE
                rvPlants.visibility = View.VISIBLE
            }
            plantAdapter.updatePlants(plants)
        } catch (e: Exception) {
            Log.e("GardenFragment", "Error showing plants list", e)
        }
    }

    private fun showPlantDetails(plant: Plant) {
         val intent = Intent(requireContext(), PlantIdentifierResultActivity::class.java).apply {
             putExtra("plant_id", plant.id)
             putExtra("plant_name", plant.name)
             putExtra("plant_image", plant.imageUrl)
         }
         startActivity(intent)
    }

    private fun showEmptyState() {
        try {
            binding.apply {
                emptyState.visibility = View.VISIBLE
                rvPlants.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e("GardenFragment", "Error showing empty state", e)
        }
    }

    override fun onDestroyView() {
        // Clean up Firebase listener
        firebaseListener?.let {
            val userId = auth.currentUser?.uid ?: return@let
            database.child("user_plants").child(userId).child("garden").removeEventListener(it)
        }
        firebaseListener = null

        // Clear binding reference
        _binding = null
        super.onDestroyView()
    }
}
