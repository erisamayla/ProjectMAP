package com.example.pharmacy

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheckoutAdapter(
    private val selectedProducts: List<ProductModel>    // Produk yang dipilih untuk checkout
) : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    // Membuat ViewHolder dengan layout item_checkout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout, parent, false)
        return CheckoutViewHolder(view)
    }

    // Menghubungkan data produk ke tampilan item
    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        val product = selectedProducts[position]
        Log.d("CheckoutAdapter", "Produk ditampilkan: $product")
        holder.image.setImageResource(product.imageRes)
        holder.title.text = product.title
        holder.quantity.text = "Qty: ${product.quantity}"
        holder.price.text = "Rp ${product.price * product.quantity}"
    }

    // Mengembalikan jumlah produk dalam daftar
    override fun getItemCount(): Int = selectedProducts.size

    // Kelas ViewHolder untuk memetakan elemen item_checkout
    class CheckoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgCheckoutItem)
        val title: TextView = view.findViewById(R.id.txt_checkout_title)
        val quantity: TextView = view.findViewById(R.id.txt_checkout_quantity)
        val price: TextView = view.findViewById(R.id.txt_checkout_price)
    }
}
