package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.OrderItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.PurchasedProductItemBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class PurchasedProductAdapter(
    private val orderItems: List<OrderItem>,
    private val products: Map<String, Product>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<PurchasedProductAdapter.PurchasedViewHolder>() {

    inner class PurchasedViewHolder(val binding: PurchasedProductItemBinding) :
        RecyclerView.ViewHolder(binding.root){
            init {
                itemView.setOnClickListener {
                    val orderItem = orderItems[adapterPosition]
                    onItemClicked(orderItem.productId)
                }
            }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PurchasedProductAdapter.PurchasedViewHolder {
        val binding =
            PurchasedProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PurchasedViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PurchasedProductAdapter.PurchasedViewHolder,
        position: Int
    ) {
        val orderItem = orderItems[position]
        val product = products[orderItem.productId]

        with(holder.binding) {
            orderItemProductCount.text = "x${orderItem.quantity}"
            product?.let {
                textProductName.text = product?.name

                val originalPrice = it.price
                val discountedPrice = it.getDiscountedPrice()

                if (it.offerPercentage > 0.0) {
                    textPrices.text = NumberFormatUtils.formatPrice(discountedPrice)
                } else {
                    textPrices.text= NumberFormatUtils.formatPrice(originalPrice)
                }

                Glide.with(holder.itemView.context).load(product.coverImageUrl)
                    .into(productImage)
            }
        }
    }

    override fun getItemCount(): Int = orderItems.size
}