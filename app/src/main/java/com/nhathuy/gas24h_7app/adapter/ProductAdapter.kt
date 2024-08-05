package com.nhathuy.gas24h_7app.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.R

class ProductAdapter(private var products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduct: ImageView = view.findViewById(R.id.iv_product)
        val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
        val tvProductPrice: TextView = view.findViewById(R.id.tv_product_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = "₫${product.price}"
        Glide.with(holder.itemView.context).load(product.coverImageUrl).into(holder.ivProduct)
    }
    fun updateData(newProduct:List<Product>){
        products = newProduct
        notifyDataSetChanged()
    }
}
