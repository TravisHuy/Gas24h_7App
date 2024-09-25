package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ItemBuyBackBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class BuyBackAdapter(private var orders: List<Order> = listOf(),
                     private var products: Map<String, Product> = emptyMap()) : RecyclerView.Adapter<BuyBackAdapter.BuyBackViewHolder>() {

    inner class BuyBackViewHolder(val binding:ItemBuyBackBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuyBackAdapter.BuyBackViewHolder {
        val binding = ItemBuyBackBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyBackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyBackAdapter.BuyBackViewHolder, position: Int) {
        val order = orders[position]
        val firstItem = order.items.firstOrNull()

        firstItem?.let {
            orderItem ->
            val product = products[orderItem.productId]
            with(holder.binding){
                product?.let {
                    val originalPrice = it.price
                    val discountedPrice = it.getDiscountedPrice()

                    if (it.offerPercentage > 0.0) {
                        tvProductPrice.text =
                            NumberFormatUtils.formatPrice(discountedPrice)
                    } else {
                        tvProductPrice.text = NumberFormatUtils.formatPrice(originalPrice)

                    }

                    Glide.with(holder.itemView.context).load(product.coverImageUrl)
                        .into(ivProduct)
                }
            }
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrderProducts(newOrders: List<Order>,newProducts: Map<String, Product>) {
        orders=newOrders
        products=newProducts
        notifyDataSetChanged()
    }

}