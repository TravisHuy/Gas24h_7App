package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.NotRatedYetItemBinding

class NotRatedYetItemAdapter(private var orders:List<Order> = listOf(),
                             private var products:Map<String,Product> = mapOf()
):RecyclerView.Adapter<NotRatedYetItemAdapter.NotRatedYetViewHolder>() {

    inner class NotRatedYetViewHolder(val binding:NotRatedYetItemBinding):RecyclerView.ViewHolder(binding.root)

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
            }
        }
    }
    override fun getItemCount(): Int = orders.size

    fun updateOrderProduct(newOrders:List<Order>,newProducts:Map<String,Product>){
        orders=newOrders
        products=newProducts
        notifyDataSetChanged()
    }
}