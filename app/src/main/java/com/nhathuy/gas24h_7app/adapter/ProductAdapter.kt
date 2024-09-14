package com.nhathuy.gas24h_7app.adapter

import android.content.Context
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
import com.nhathuy.gas24h_7app.databinding.ItemProductBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class ProductAdapter(private var products: List<Product>,private  val listener: ProductClickListener) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(position !=RecyclerView.NO_POSITION){
                    listener.onProductClick(products[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        with(holder.binding){
            tvProductName.text=product.name

            tvProductPrice.text = NumberFormatUtils.formatPrice(product.price)
            Glide.with(holder.itemView.context).load(product.coverImageUrl).into(ivProduct)
            if(product.offerPercentage>0.0){
                tvProductOfferPercentage.text= String.format("-%.0f%%", product.offerPercentage)
            }
            else{
                tvProductOfferPercentage.visibility=View.GONE
            }
        }
    }
    fun updateData(newProduct:List<Product>){
        products = newProduct
        notifyDataSetChanged()
    }
}
