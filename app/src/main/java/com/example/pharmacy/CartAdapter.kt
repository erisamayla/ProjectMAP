package com.example.pharmacy

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(
    private val cartItems: MutableList<ProductModel>, // Mutable untuk memodifikasi list
    private val onQuantityChange: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        val context = holder.itemView.context

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
            if (item.quantity > 1) {
                // Kurangi quantity langsung jika masih lebih dari 1
                item.quantity--
                notifyItemChanged(position) // Update item di adapter
                onQuantityChange() // Callback untuk update total harga
            } else {
                // Jika quantity == 1, tampilkan dialog konfirmasi
                showDeleteConfirmationDialog(context, item, position)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    // Tampilkan dialog konfirmasi
    private fun showDeleteConfirmationDialog(context: Context, item: ProductModel, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Produk")
            .setMessage("Apakah Anda yakin ingin menghapus produk ini dari keranjang?")
            .setPositiveButton("Ya") { _, _ ->
                // Hapus item dari keranjang dan database
                deleteProductFromCart(item)
                cartItems.removeAt(position)
                notifyItemRemoved(position)
                onQuantityChange() // Update total harga
                Toast.makeText(context, "${item.title} telah dihapus dari keranjang.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Jangan lakukan apapun
            }
            .show()
    }

    // Hapus item dari Firestore
    private fun deleteProductFromCart(item: ProductModel) {
        val cartRef = FirebaseFirestore.getInstance().collection("cart")

        cartRef.whereEqualTo("title", item.title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    cartRef.document(document.id).delete() // Hapus dokumen
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgCartItem)
        val title: TextView = view.findViewById(R.id.tvCartItemTitle)
        val price: TextView = view.findViewById(R.id.tvCartItemPrice)
        val quantity: TextView = view.findViewById(R.id.tvCartItemQuantity)
    }
}
