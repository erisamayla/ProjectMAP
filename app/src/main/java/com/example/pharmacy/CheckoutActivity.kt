package com.example.pharmacy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CheckoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Ambil produk yang dipilih dari Intent
        val selectedProducts = intent.getParcelableArrayListExtra<ProductModel>("selectedProducts") ?: listOf()
        Log.d("CheckoutActivity", "Produk diterima: $selectedProducts")

        // Hitung total harga dari produk yang dipilih
        val totalPrice = selectedProducts.sumOf { it.price * it.quantity }

        // Atur RecyclerView untuk menampilkan produk yang dipilih
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_selected_products)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CheckoutAdapter(selectedProducts)

        // Tampilkan total harga
        val totalPriceTextView = findViewById<TextView>(R.id.text_total_price)
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

            // Buat orderId
            val orderId = "ORDER-${System.currentTimeMillis()}"

            // Buat data checkout
            val checkoutData = CheckoutModel(
                orderId = orderId,
                products = selectedProducts,
                totalPrice = totalPrice,
                shippingMethod = selectedShipping,
                paymentMethod = selectedPayment,
                orderDate = System.currentTimeMillis()
            )

            // Simpan ke Firestore
            val db = Firebase.firestore
            db.collection("checkout").document(orderId).set(checkoutData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pesanan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, OrderDoneActivity::class.java)
                    intent.putExtra("checkoutData", checkoutData)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // koneksi ke firebase checkout
            db.collection("checkout").document(orderId).set(checkoutData)
                .addOnSuccessListener {

                    // Hapus produk di keranjang
                    db.collection("cart").get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                document.reference.delete() // Hapus produk di keranjang
                            }
                            Toast.makeText(this, "Checkout berhasil!", Toast.LENGTH_SHORT).show()

                            // Navigasi ke halaman OrderDoneActivity
                            val intent = Intent(this, OrderDoneActivity::class.java)
                            intent.putExtra("checkoutData", checkoutData)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal mengosongkan keranjang: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // saat di klik back ada konfirmasi
    override fun onBackPressed() {
        // Buat dialog konfirmasi
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin meninggalkan halaman ini? Perubahan Anda mungkin tidak tersimpan.")
            .setPositiveButton("Ya") { dialog, which ->
                // Panggil super.onBackPressed() untuk melanjutkan aksi back
                super.onBackPressed()
            }
            .setNegativeButton("Tidak") { dialog, which ->
                // Tutup dialog
                dialog.dismiss()
            }
            .show()
    }
}
