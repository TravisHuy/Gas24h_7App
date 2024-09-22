package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.OrderItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.PurchaseorderProductItemBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class PurchasedOrderProductItemAdapter(private var orderItems: List<OrderItem> = listOf(),
                                       private var products: Map<String, Product> = emptyMap(),
                                       private var onItemClicked: (String) -> Unit):RecyclerView.Adapter<PurchasedOrderProductItemAdapter.PurchasedOrderProductViewHolder>() {

    inner class PurchasedOrderProductViewHolder(val binding:PurchaseorderProductItemBinding):RecyclerView.ViewHolder(binding.root){
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
    ): PurchasedOrderProductItemAdapter.PurchasedOrderProductViewHolder {
        val binding = PurchaseorderProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PurchasedOrderProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PurchasedOrderProductItemAdapter.PurchasedOrderProductViewHolder,
        position: Int
    ) {
        val orderItem = orderItems[position]
        val product = products[orderItem.productId]

        with(holder.binding){
            product?.let {
                purchasedOrderItemName.text = it.name
                purchasedOrderCountItem.text = "x${orderItem.quantity}"

                val originalPrice = it.price
                val discountedPrice = it.getDiscountedPrice()

                if (it.offerPercentage > 0.0) {
                    purchasedOrderOriginalPrice.text = NumberFormatUtils.formatPrice(originalPrice)
                    purchasedOrderDiscountPrice.text =
                        NumberFormatUtils.formatPrice(discountedPrice)
                } else {
                    purchasedOrderDiscountPrice.text = NumberFormatUtils.formatPrice(originalPrice)
                    purchasedOrderOriginalPrice.visibility = View.GONE

                }

                Glide.with(holder.itemView.context).load(product.coverImageUrl)
                    .into(purchaseOrderImage)
            }
        }
    }

    override fun getItemCount(): Int = orderItems.size

    fun updateOrderItems(newOrderItems: List<OrderItem>, newProducts: Map<String, Product>) {
        orderItems = newOrderItems
        products = newProducts
        notifyDataSetChanged()
    }
}