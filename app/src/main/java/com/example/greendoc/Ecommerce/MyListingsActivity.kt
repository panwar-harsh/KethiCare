package com.example.greendoc.Ecommerce

import Tool
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.greendoc.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyListingsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyListingsAdapter
    private val toolList = mutableListOf<Tool>()
    private lateinit var database: DatabaseReference
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_listings)

        recyclerView = findViewById(R.id.myListingsRecyclerView)
        adapter = MyListingsAdapter(this, toolList)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference.child("tools")

        fetchMyListings()
    }

    private fun fetchMyListings() {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
            return
        }

        database.orderByChild("sellerId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    toolList.clear()
                    for (toolSnapshot in snapshot.children) {
                        val tool = toolSnapshot.getValue(Tool::class.java)
                        tool?.let {
                            it.id = toolSnapshot.key ?: ""
                            toolList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MyListingsActivity, "Failed to load listings!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
