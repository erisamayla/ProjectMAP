package com.example.pharmacy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCheckout: Button
    private lateinit var tvTotalPrice: TextView
    private val cartItems = mutableListOf<ProductModel>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        btnCheckout = findViewById(R.id.btnCheckout)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Real-time update untuk data keranjang
        observeCartItems()

        // Tombol Checkout
        btnCheckout.setOnClickListener {
            if (cartItems.isEmpty()) {
                Toast.makeText(
                    this,
                    "Keranjang kosong. Silakan tambahkan barang terlebih dahulu.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Filter produk yang memiliki quantity > 0
            val selectedProducts = cartItems.filter { it.quantity > 0 }
            Log.d("SelectedProducts", "Produk yang dikirim: $selectedProducts")

            if (selectedProducts.isEmpty()) {
                Toast.makeText(
                    this,
                    "Tidak ada barang di keranjang dengan jumlah lebih dari 0.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Kirimkan data ke CheckoutActivity
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putParcelableArrayListExtra("selectedProducts", ArrayList(selectedProducts))
            startActivity(intent)
        }
    }

    private fun calculateTotalPrice(): Int {
        var totalPrice = 0
        for (item in cartItems) {
            totalPrice += item.price * item.quantity
        }
        return totalPrice
    }

    private fun updateTotalPrice() {
        val totalPrice = calculateTotalPrice() // Hitung total harga
        val formattedPrice = "Total: Rp$totalPrice" // Format ke string
        tvTotalPrice.text = formattedPrice // Set ke TextView
    }

    private fun observeCartItems() {
        val cartRef = db.collection("cart")

        val adapter = CartAdapter(cartItems) {
            updateTotalPrice() // Callback untuk memperbarui total harga
        }
        recyclerView.adapter = adapter

        cartRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Error mendapatkan data keranjang", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                cartItems.clear()
                for (document in snapshot.documents) {
                    val product = ProductModel(
                        imageRes = document.getLong("imageRes")?.toInt() ?: R.drawable.pharmacy,
                        title = document.getString("title") ?: "",
                        price = document.getLong("price")?.toInt() ?: 0,
                        category = document.getString("category") ?: "",
                        quantity = document.getLong("quantity")?.toInt() ?: 0
                    )
                    cartItems.add(product)
                }
                Log.d("CartItems", "Data keranjang: $cartItems")
                recyclerView.adapter?.notifyDataSetChanged()
                updateTotalPrice() // Perbarui total harga setelah data dimuat
            }
        }
    }
}
