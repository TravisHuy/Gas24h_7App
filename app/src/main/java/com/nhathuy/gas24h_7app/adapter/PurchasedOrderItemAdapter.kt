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
    private var orders: List<Order> = mutableListOf(),
    private var products: Map<String, Product> = emptyMap(),
    private val listener: OrderClickListener?
) : RecyclerView.Adapter<PurchasedOrderItemAdapter.PurchaseOrderViewHolder>() {


    private var currentStatus = "PENDING"

    inner class PurchaseOrderViewHolder(val binding: PurchasedorderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onOrderClick(orders[position])
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
        val order = orders[position]
        val firstItem = order.items.firstOrNull()

        firstItem?.let { orderItem ->
            val product = products[orderItem.productId]

            with(holder.binding) {
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
                    val totalPrice =
                        orderItem.quantity * (if (it.offerPercentage > 0) it.getDiscountedPrice() else it.price)

                    purchasedOrderTotalPrice.text = NumberFormatUtils.formatPrice(totalPrice)

                    purchaseCountProduct.text =
                        holder.binding.root.context.getString(R.string.count_product, orderItem.quantity)

                    when (currentStatus) {
                        "PENDING" -> btnStatus.text = "Đang xử lý"
                        "PROCESSING" ->{
                            btnStatus.text = "Đang tiếp nhận"
                            btnStatus.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_light_primary))
                        }
                        "SHIPPED" -> {
                            btnStatus.text = "Đã tiếp nhận"
                            btnStatus.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_light_primary))
                        }
                        "DELIVERED" ->{
                            btnStatus.text = "Đã giao"
                            btnStatus.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_light_primary))
                        }
                        "CANCELLED" -> btnStatus.text = "Đã hủy"
                        else -> btnStatus.text = "Xem chi tiết"
                    }


                }
            }
        }


    }

    override fun getItemCount(): Int = orders.size
    fun updateOrders(newOrders: List<Order>) {
        orders=newOrders
        notifyDataSetChanged()
    }
    fun updateProducts(newProducts: Map<String, Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
    fun updateStatus(newStatus:String){
        currentStatus=newStatus
        notifyDataSetChanged()
    }
}

interface OrderClickListener {
    fun onOrderClick(order: Order)
}