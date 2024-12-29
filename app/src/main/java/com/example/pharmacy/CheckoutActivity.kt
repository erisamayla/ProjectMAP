package com.example.pharmacy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CheckoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Ambil produk yang dipilih dari Intent
        val selectedProducts = intent.getParcelableArrayListExtra<ProductModel>("selectedProducts") ?: listOf()
        Log.d("CheckoutActivity", "Produk diterima: $selectedProducts")

        // Atur RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_selected_products)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CheckoutAdapter(selectedProducts)

        // Total Harga
        val totalPriceTextView = findViewById<TextView>(R.id.text_total_price)
        val totalPrice = selectedProducts.sumOf { it.price * it.quantity }
        totalPriceTextView.text = "Total: Rp $totalPrice"

        // Opsi Pengiriman
        val shippingSpinner = findViewById<Spinner>(R.id.spinner_shipping_options)
        val shippingOptions = listOf("JNE", "J&T", "GO-SEND", "TIKI")
        shippingSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, shippingOptions)

        // Opsi Pembayaran
        val paymentSpinner = findViewById<Spinner>(R.id.spinner_payment_options)
        val paymentOptions = listOf("Transfer Bank", "COD", "e-Wallet")
        paymentSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentOptions)

        // Tombol Buat Pesanan
        val btnPlaceOrder = findViewById<Button>(R.id.btn_place_order)
        btnPlaceOrder.setOnClickListener {
            // Ambil pilihan metode pengiriman dan pembayaran
            val selectedShipping = shippingSpinner.selectedItem?.toString()
            val selectedPayment = paymentSpinner.selectedItem?.toString()

            // Validasi apakah pengguna sudah memilih pengiriman dan pembayaran
            if (selectedShipping.isNullOrEmpty() || selectedPayment.isNullOrEmpty()) {
                Toast.makeText(this, "Silakan pilih metode pengiriman dan pembayaran!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Logika untuk memproses pesanan (misalnya, simpan data ke database)
            Log.d("CheckoutActivity", "Pesanan diproses dengan: $selectedShipping, $selectedPayment")

            // Pindah ke OrderDoneActivity setelah validasi selesai
            val intent = Intent(this, OrderDoneActivity::class.java)
            startActivity(intent)
            finish() // Tutup halaman CheckoutActivity
        }
    }
}
