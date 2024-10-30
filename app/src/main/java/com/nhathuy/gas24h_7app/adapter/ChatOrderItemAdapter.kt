package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ChatOrderItemBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class ChatOrderItemAdapter(private var orders:List<Order> = emptyList(),
                           private var products: Map<String, Product> = emptyMap(),
                           private var onItemClicked : (String) -> Unit
):RecyclerView.Adapter<ChatOrderItemAdapter.ChatOrderItemViewHolder>() {

    inner class ChatOrderItemViewHolder (val binding:ChatOrderItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatOrderItemAdapter.ChatOrderItemViewHolder {
        val binding = ChatOrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChatOrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChatOrderItemAdapter.ChatOrderItemViewHolder,
        position: Int
    ) {
        val order = orders[position]

        with(holder.binding){
            tvOrderId.text= order.id

            val layoutManager = LinearLayoutManager(holder.itemView.context)
            orderItemsRec.layoutManager= layoutManager

            val orderItemsAdapter = PurchasedProductAdapter(order.items,products){
                onItemClicked(order.id)
            }
            orderItemsRec.adapter=orderItemsAdapter

            val totalOrderPrice = order.items.sumOf {
               it.price*it.quantity
            }
            totalTvPrice.text=NumberFormatUtils.formatPrice(totalOrderPrice)
//            totalChatPrice.text= NumberFormatUtils.formatPrice(order.totalAmount)
        }
    }

    override fun getItemCount(): Int  = orders.size

    fun updateDate(newOrders: List<Order>, newProducts: Map<String, Product>){
        orders = newOrders
        products = newProducts
        notifyDataSetChanged()
    }
}