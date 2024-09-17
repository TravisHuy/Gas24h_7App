package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.PurchasedorderItemBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class PurchasedOrderItemAdapter(
    private val order: MutableList<Order> = mutableListOf(),
    private val orderItems: MutableList<OrderItem> = mutableListOf(),
    private val products: MutableMap<String, Product> = mutableMapOf(),
    private val listener: OrderClickListener
) : RecyclerView.Adapter<PurchasedOrderItemAdapter.PurchaseOrderViewHolder>() {

    inner class PurchaseOrderViewHolder(val binding: PurchasedorderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onOrderClick(order[position])
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PurchasedOrderItemAdapter.PurchaseOrderViewHolder {
        val binding =
            PurchasedorderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PurchaseOrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PurchasedOrderItemAdapter.PurchaseOrderViewHolder,
        position: Int
    ) {
        val order = orderItems[position]
        val product = products[order.productId]

        with(holder.binding) {
            product?.let {
                purchasedOrderItemName.text = it.name
                purchasedOrderCountItem.text = "x${orderItems.size}"

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
                val totalPrice =
                    order.quantity * (if (it.offerPercentage > 0) it.getDiscountedPrice() else it.price)

                purchasedOrderTotalPrice.text = NumberFormatUtils.formatPrice(totalPrice)

                purchaseCountProduct.text =
                    holder.binding.root.context.getString(R.string.count_product, order.quantity)
            }
        }
    }

    override fun getItemCount(): Int = order.size

}

interface OrderClickListener {
    fun onOrderClick(order: Order)
}