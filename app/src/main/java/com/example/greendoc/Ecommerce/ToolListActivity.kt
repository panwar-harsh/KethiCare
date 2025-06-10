package com.example.greendoc.Ecommerce

import Tool
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greendoc.R
import com.google.firebase.database.*

class ToolListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ToolAdapter
    private lateinit var searchEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var database: FirebaseDatabase
    private lateinit var toolsRef: DatabaseReference
    private val toolList = mutableListOf<Tool>()
    private var toolsListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_list)

        database = FirebaseDatabase.getInstance()
        toolsRef = database.reference.child("tools")

        recyclerView = findViewById(R.id.recyclerViewTools)
        searchEditText = findViewById(R.id.searchEditText)
        categorySpinner = findViewById(R.id.categorySpinner)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ToolAdapter(toolList) { tool -> openToolDetail(tool) }
        recyclerView.adapter = adapter

        setupCategorySpinner()
        fetchTools()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTools()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupCategorySpinner() {
        val categories = listOf("All", "Tractors", "Plows", "Irrigation", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterTools()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchTools() {
        toolsListener?.let { toolsRef.removeEventListener(it) } // Prevent duplicate listeners

        toolsListener = toolsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toolList.clear()
                for (toolSnapshot in snapshot.children) {
                    val tool = toolSnapshot.getValue(Tool::class.java)
                    tool?.let {
                        it.id = toolSnapshot.key ?: ""
                        Log.d("ToolListActivity", "Fetched Tool: $it")
                        toolList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ToolListActivity, "Failed to load tools", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterTools() {
        val searchText = searchEditText.text.toString().lowercase().trim()
        val selectedCategory = categorySpinner.selectedItem.toString()

        val filteredList = toolList.filter {
            (selectedCategory == "All" || it.category == selectedCategory) &&
                    (searchText.isEmpty() || it.name.lowercase().contains(searchText))
        }

        adapter.updateList(filteredList)
    }

    private fun openToolDetail(tool: Tool) {
        val intent = Intent(this, ToolDetailActivity::class.java)
        intent.putExtra("toolId", tool.id)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        toolsListener?.let { toolsRef.removeEventListener(it) } // Clean up listener
    }
}
