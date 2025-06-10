package com.example.greendoc.Ecommerce


import Tool
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greendoc.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeScreenActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolAdapter: ToolAdapter
    private lateinit var searchEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var fabUpload: FloatingActionButton
    private lateinit var database: FirebaseDatabase
    private val toolList = mutableListOf<Tool>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        database = FirebaseDatabase.getInstance()

        recyclerView = findViewById(R.id.recyclerViewTools)
        searchEditText = findViewById(R.id.searchEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        fabUpload = findViewById(R.id.fabUpload)

        recyclerView.layoutManager = LinearLayoutManager(this)
        toolAdapter = ToolAdapter(toolList) { tool ->
            Log.d("HomeScreenActivity", "Clicked Tool: $tool") // 🔍 Debug log
            val intent = Intent(this, ToolDetailActivity::class.java)
            intent.putExtra("tool", tool)  // ✅ Pass the full Tool object
            startActivity(intent)
        }


        recyclerView.adapter = toolAdapter

        loadCategories()
        loadTools()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTools()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterTools()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fabUpload.setOnClickListener {
            startActivity(Intent(this, UploadToolActivity::class.java))
        }
    }

    private fun loadCategories() {
        val categories = listOf("All", "Tractors", "Plows", "Irrigation", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter
    }

    private fun loadTools() {
        Log.d("HomeScreenActivity", "Loading tools from Firebase...") // Debug log
        val toolsRef = database.reference.child("tools")

        toolsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toolList.clear()
                for (toolSnapshot in snapshot.children) {
                    val tool = toolSnapshot.getValue(Tool::class.java)
                    tool?.let {
                        it.id = toolSnapshot.key ?: ""
                        toolList.add(it)
                    }
                }
                Log.d("HomeScreenActivity", "Tools loaded: ${toolList.size}") // Debug log

                // Ensure "All" is selected and display all tools initially
                categorySpinner.setSelection(0)  // Select "All" in spinner
                toolAdapter.updateList(toolList) // Show all tools initially
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RealtimeDB", "Error loading tools", error.toException())
            }
        })
    }
    private fun filterTools() {
        val searchQuery = searchEditText.text.toString().lowercase()
        val selectedCategory = categorySpinner.selectedItem.toString()
        val filteredList = toolList.filter {
            (it.name.lowercase().contains(searchQuery) || searchQuery.isEmpty()) &&
                    (selectedCategory == "All" || it.category == selectedCategory)
        }
        toolAdapter.updateList(filteredList)
    }
}
