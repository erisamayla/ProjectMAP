import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pharmacy.CartActivity
import com.example.pharmacy.ProductModel
import com.example.pharmacy.R

class ProductAdapter(
    private val allProducts: List<ProductModel>,
    private val category: String
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val filteredProducts = allProducts.filter { it.category.equals(category, ignoreCase = true) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredProducts[position]
        holder.image.setImageResource(product.imageRes)
        holder.title.text = product.title
        holder.price.text = "Rp ${product.price}"
        holder.quantity.text = product.quantity.toString()

        // Tambah kuantitas
        holder.btnIncrease.setOnClickListener {
            product.quantity++
            holder.quantity.text = product.quantity.toString()
        }

        // Kurangi kuantitas
        holder.btnDecrease.setOnClickListener {
            if (product.quantity > 0) {
                product.quantity--
                holder.quantity.text = product.quantity.toString()
            }
        }
    }

    override fun getItemCount(): Int = filteredProducts.size

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txt_title)
        val image: ImageView = view.findViewById(R.id.imageview)
        val price: TextView = view.findViewById(R.id.txt_harga)
        val quantity: TextView = view.findViewById(R.id.txt_quantity)
        val btnIncrease: TextView = view.findViewById(R.id.btn_increase)
        val btnDecrease: TextView = view.findViewById(R.id.btn_decrease)
    }
}
