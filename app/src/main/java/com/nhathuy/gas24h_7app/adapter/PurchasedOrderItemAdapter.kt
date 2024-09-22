package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
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
                    listener?.onOrderClick(orders[position].id)
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

            with(holder.binding) {
                val layoutManager = LinearLayoutManager(holder.itemView.context)
                purchaseOrderProductRec.layoutManager = layoutManager
                val orderItemsAdapter = PurchasedOrderProductItemAdapter(order.items, products) {productId ->
                    listener?.onProductClick(order.id, productId)
                }
                purchaseOrderProductRec.adapter = orderItemsAdapter

                val totalPrice = order.items.sumOf { orderItem ->
                    val product = products[orderItem.productId]
                    orderItem.quantity * (product?.let { if (it.offerPercentage > 0) it.getDiscountedPrice() else it.price } ?: 0.0)
                }
                purchasedOrderTotalPrice.text = NumberFormatUtils.formatPrice(totalPrice)


                val totalQuantity = order.items.sumOf { it.quantity }
                purchaseCountProduct.text =
                    holder.binding.root.context.getString(
                        R.string.count_product,
                        totalQuantity
                    )

                when (currentStatus) {
                    "PENDING" -> btnStatus.text = "Đang xử lý"
                    "PROCESSING" -> {
                        btnStatus.text = "Đang tiếp nhận"
                        btnStatus.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_light_primary))
                    }

                    "SHIPPED" -> {
                        btnStatus.text = "Đã tiếp nhận"
                        btnStatus.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_light_primary))
                    }

                    "DELIVERED" -> {
                        btnStatus.text = "Đã giao"
                        btnStatus.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_light_primary))
                    }

                    "CANCELLED" -> btnStatus.text = "Đã hủy"
                    else -> btnStatus.text = "Xem chi tiết"
                }


            }
        }


    }

    override fun getItemCount(): Int = orders.size
    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    fun updateProducts(newProducts: Map<String, Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    fun updateStatus(newStatus: String) {
        currentStatus = newStatus
        notifyDataSetChanged()
    }
}

interface OrderClickListener {
    fun onOrderClick(orderId: String)
    fun onProductClick(orderId: String, productId: String)
}