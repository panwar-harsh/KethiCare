package com.example.greendoc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greendoc.Adapters.PlantAdapter
import com.example.greendoc.databinding.BottomSheetSelectPlantBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SelectPlantBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSelectPlantBinding? = null
    private val binding get() = _binding!!

    private lateinit var plantAdapter: PlantAdapter
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private var firebaseListener: ValueEventListener? = null

    private var onPlantSelected: ((Plant) -> Unit)? = null

    fun setOnPlantSelectedListener(listener: (Plant) -> Unit) {
        onPlantSelected = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSelectPlantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadUserPlants()
    }

    private fun setupRecyclerView() {
        plantAdapter = PlantAdapter(emptyList()) { plant ->
            onPlantSelected?.invoke(plant)
            dismiss()
        }

        binding.recyclerViewPlants.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = plantAdapter
        }
    }

    private fun loadUserPlants() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("PLANT_LOG", "loadUserPlants() called")

        firebaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plantList = mutableListOf<Plant>()
                for (plantSnap in snapshot.children) {
                    val plant = plantSnap.getValue(Plant::class.java)
                    plant?.let {
                        if (!it.deleted) {
                            it.id = plantSnap.key ?: ""
                            plantList.add(it)
                        }
                    }
                }
                Log.d("PLANT_LOG", "Loaded plants: ${plantList.size}")
                plantAdapter.updatePlants(plantList)
                binding.emptyState.visibility = if (plantList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading plants: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        database.child("user_plants").child(userId).child("garden")
            .addValueEventListener(firebaseListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val userId = auth.currentUser?.uid ?: return
        firebaseListener?.let {
            database.child("user_plants").child(userId).child("garden").removeEventListener(it)
        }
        _binding = null
    }

    companion object {
        fun newInstance(): SelectPlantBottomSheet {
            return SelectPlantBottomSheet()
        }
    }
}
