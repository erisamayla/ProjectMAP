package com.example.pharmacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val cartItems: List<ProductModel>,
    private val onQuantityChange: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.image.setImageResource(item.imageRes)
        holder.title.text = item.title
        holder.price.text = "Rp ${item.price}"
        holder.quantity.text = "Jumlah: ${item.quantity}"

        // Handle tombol tambah (+)
        holder.itemView.findViewById<TextView>(R.id.btnCartIncrease).setOnClickListener {
            item.quantity++ // Tambah quantity
            notifyItemChanged(position) // Update item di adapter
            onQuantityChange() // Callback untuk update total harga
        }

        // Handle tombol kurang (-)
        holder.itemView.findViewById<TextView>(R.id.btnCartDecrease).setOnClickListener {
            if (item.quantity > 0) {
                item.quantity-- // Kurangi quantity
                notifyItemChanged(position) // Update item di adapter
                onQuantityChange() // Callback untuk update total harga
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgCartItem)
        val title: TextView = view.findViewById(R.id.tvCartItemTitle)
        val price: TextView = view.findViewById(R.id.tvCartItemPrice)
        val quantity: TextView = view.findViewById(R.id.tvCartItemQuantity)
    }
}
