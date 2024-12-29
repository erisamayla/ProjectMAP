package com.example.pharmacy

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore
        db.collection("Pharmacy")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val data = ArrayList<DataViewModel>().apply {
            add(DataViewModel(R.drawable.alatkesehatan, "Alat Kesehatan"))
            add(DataViewModel(R.drawable.vitamin, "Vitamin"))
            add(DataViewModel(R.drawable.diare, "Diare"))
        }

        val adapter = CustomAdapter(data) { selectedCategory ->
            Log.d(TAG, "Selected category: ${selectedCategory.category}")
            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra("category", selectedCategory.category) // Ambil kategori dari data
            startActivity(intent)
        }

        recyclerview.adapter = adapter
    }
}