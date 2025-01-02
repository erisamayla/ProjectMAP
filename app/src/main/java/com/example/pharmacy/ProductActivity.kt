package com.example.pharmacy

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        // Daftar semua produk
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

        // Atur RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.productrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ProductAdapter(allProducts, category).apply {
            // Callback ketika tombol Add diklik
            onAddToCart = { product ->
                if (product.quantity > 0) {
                    addToCart(product)
                } else {
                    Toast.makeText(
                        this@ProductActivity,
                        "Tambahkan minimal 1 produk sebelum ke keranjang!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        recyclerView.adapter = adapter

        val btnViewCart = findViewById<Button>(R.id.btn_view_cart)

        // Tombol untuk melihat keranjang
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

            // Simpan ke Firestore dan pindah ke CartActivity
            saveCartToFirestore(selectedProducts)
        }
    }

    // Fungsi untuk menambahkan produk ke Firestore
    private fun addToCart(product: ProductModel) {
        val cartRef = FirebaseFirestore.getInstance().collection("cart")

        cartRef.whereEqualTo("title", product.title).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Produk belum ada di keranjang, tambahkan sebagai entri baru
                    val productMap = mapOf(
                        "title" to product.title,
                        "price" to product.price,
                        "category" to product.category,
                        "quantity" to product.quantity,
                        "imageRes" to product.imageRes
                    )
                    cartRef.add(productMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "${product.title} berhasil ditambahkan ke keranjang!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    // Produk sudah ada di keranjang, perbarui jumlahnya
                    for (document in documents) {
                        val existingQuantity = document.getLong("quantity") ?: 0
                        val updatedQuantity = existingQuantity + product.quantity

                        cartRef.document(document.id)
                            .update("quantity", updatedQuantity)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "${product.title} berhasil diperbarui di keranjang!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Gagal menambahkan ke keranjang: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    // Fungsi untuk menyimpan semua produk ke Firestore
    private fun saveCartToFirestore(selectedProducts: List<ProductModel>) {
        val cartRef = FirebaseFirestore.getInstance().collection("cart")

        // Loop produk yang dipilih
        for (product in selectedProducts) {
            cartRef.whereEqualTo("title", product.title).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Tambahkan produk baru jika tidak ada
                        val productMap = mapOf(
                            "title" to product.title,
                            "price" to product.price,
                            "category" to product.category,
                            "quantity" to product.quantity,
                            "imageRes" to product.imageRes
                        )
                        cartRef.add(productMap)
                    } else {
                        // Update jumlah produk jika sudah ada
                        for (document in documents) {
                            val existingQuantity = document.getLong("quantity") ?: 0
                            val updatedQuantity = existingQuantity + product.quantity

                            cartRef.document(document.id)
                                .update("quantity", updatedQuantity)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Gagal menyimpan data keranjang", e)
                }
        }

        // Pindah ke CartActivity
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }
}
