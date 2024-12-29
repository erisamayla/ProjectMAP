package com.example.pharmacy

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        // Semua produk
        val allProducts = listOf(
            ProductModel(R.drawable.kursi, "Kursi Roda", 900000, "Alat Kesehatan"),
            ProductModel(R.drawable.tabung, "Tabung Oksigen", 200000, "Alat Kesehatan"),
            ProductModel(R.drawable.neurobion, "Neurobion", 48000, "Vitamin"),
            ProductModel(R.drawable.blacmores, "Blackmores", 160000, "Vitamin"),
            ProductModel(R.drawable.diapet, "Diapet", 7000, "Diare"),
            ProductModel(R.drawable.diatabs, "Diatabs", 10000, "Diare")
        )

        // Ambil kategori dari Intent
        val category = intent.getStringExtra("category") ?: ""

        // Atur RecyclerView dengan semua produk dan kategori
        val recyclerView = findViewById<RecyclerView>(R.id.productrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProductAdapter(allProducts, category)
        val adapter = ProductAdapter(allProducts, category)
        recyclerView.adapter = adapter

        val btnViewCart = findViewById<Button>(R.id.btn_view_cart)

        // Di dalam ProductActivity
        btnViewCart.setOnClickListener {
            val selectedProducts = allProducts.filter { it.quantity > 0 }

            if (selectedProducts.isEmpty()) {
                Toast.makeText(
                    this,
                    "Keranjang kosong. Silakan tambahkan barang terlebih dahulu.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Simpan ke Firebase
            val cartRef = FirebaseFirestore.getInstance().collection("cart")
            cartRef.get()
                .addOnSuccessListener { documents ->
                    // Hapus data sebelumnya
                    for (document in documents) {
                        cartRef.document(document.id).delete()
                    }

                    // Tambahkan produk yang baru
                    for (product in selectedProducts) {
                        val productMap = mapOf(
                            "title" to product.title,
                            "price" to product.price,
                            "category" to product.category,
                            "quantity" to product.quantity,
                            "imageRes" to product.imageRes
                        )
                        cartRef.add(productMap)
                    }

                    // Pindah ke CartActivity
                    Log.d("CartNavigation", "Tombol Lihat Keranjang ditekan")
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Gagal menyimpan data keranjang", e)
                }
        }

    }
}
