package com.nhathuy.gas24h_7app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.NotRatedYetItemBinding

class NotRatedYetItemAdapter(private var orders:List<Order> = mutableListOf(),
                             private var products:Map<String,Product> = mutableMapOf(),
                             private var listener: NotRatedYetListener?
):RecyclerView.Adapter<NotRatedYetItemAdapter.NotRatedYetViewHolder>() {

    inner class NotRatedYetViewHolder(val binding:NotRatedYetItemBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnReview.setOnClickListener {
                val position = adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    val order = orders[position]
                    Log.d("NotRatedYetItemAdapter", "Button clicked for order ID: ${order.id}")
                    listener?.onClick(order.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotRatedYetViewHolder {
        val binding = NotRatedYetItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotRatedYetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotRatedYetViewHolder, position: Int) {
        val order = orders[position]
        val firstItem = order.items.firstOrNull()

        firstItem?.let {
            orderItem ->
            val product = products[orderItem.productId]
            with(holder.binding){
                reviewNotYetCountItem.text = "x${order.items.size}"
                product?.let {
                    reviewItemName.text = it.name
                    Glide.with(holder.itemView.context).load(product.coverImageUrl)
                        .into(reviewOrderImage)
                }
                btnReview.tag= order.id
            }
        }
    }
    override fun getItemCount(): Int = orders.size

    fun updateOrderProduct(newOrders:List<Order>,newProducts:Map<String,Product>){
        orders=newOrders
        products=newProducts
        notifyDataSetChanged()
        Log.d("NotRatedYetItemAdapter", "Updated orders: ${orders.map { it.id }}")
    }
}
interface NotRatedYetListener{
    fun onClick(orderId:String)
}