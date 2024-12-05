package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.OrderItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.InformationOrderItemBinding

class InformationOderItemAdapter(private val orderItems: List<OrderItem>,
                                 private val products: Map<String, Product>)
    :RecyclerView.Adapter<InformationOderItemAdapter.InformationOrderItemViewHolder>() {

    inner class InformationOrderItemViewHolder(val binding:InformationOrderItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InformationOrderItemViewHolder {
        val binding = InformationOrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InformationOrderItemViewHolder(binding)
    }

    override fun getItemCount(): Int = orderItems.size

    override fun onBindViewHolder(holder: InformationOrderItemViewHolder, position: Int) {
        val orderItem = orderItems[position]
        val product = products[orderItem.productId]

        with(holder.binding){
            tvCount.text = holder.itemView.context.getString(R.string.order_count,orderItem.quantity)
            product?.let {
                tvInformationOrderName.text = product.name
            }
        }
    }


}