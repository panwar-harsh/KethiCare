package com.example.greendoc.utils


import com.example.greendoc.Plant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

fun migratePlantData() {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: return

    val database = FirebaseDatabase.getInstance().reference
    val userPlantsRef = database.child("user_plants").child(userId)

    userPlantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { plantSnapshot ->
                val plant = plantSnapshot.getValue(Plant::class.java)
                plant?.let {
                    database.child("user_plants")
                        .child(userId)
                        .child("history")
                        .child(plantSnapshot.key!!)
                        .setValue(it)

                    if (!it.deleted) {
                        database.child("user_plants")
                            .child(userId)
                            .child("garden")
                            .child(plantSnapshot.key!!)
                            .setValue(it)
                    }
                }
            }
            userPlantsRef.removeValue()
        }

        override fun onCancelled(error: DatabaseError) {
            println("Migration failed: ${error.message}")
        }
    })
}