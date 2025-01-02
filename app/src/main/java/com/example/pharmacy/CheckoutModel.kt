package com.example.pharmacy

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckoutModel(
    val orderId: String,
    val products: List<ProductModel>, // Daftar produk yang dipilih
    val totalPrice: Int,              // Total harga
    val shippingMethod: String,       // Metode pengiriman
    val paymentMethod: String,        // Metode pembayaran
    val orderDate: Long               // Waktu pemesanan dalam epoch time
) : Parcelable
