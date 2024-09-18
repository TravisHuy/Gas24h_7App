package com.nhathuy.gas24h_7app.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.PurchasedOrderPendingConfirmationItemBinding

class PendingConfirmationAdapter(private var orders:List<Order> = emptyList(),
                                 private var users: Map<String,User> = emptyMap(),
                                 private var products: Map<String,Product> = emptyMap(),
                                 private var selectedItemIds: Set<String> = setOf(),
                                 private var onItemChecked:(String,Boolean) -> Unit
):RecyclerView.Adapter<PendingConfirmationAdapter.PendingConfirmationViewHolder>() {

    inner class PendingConfirmationViewHolder(val binding:PurchasedOrderPendingConfirmationItemBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingConfirmationAdapter.PendingConfirmationViewHolder {
        val binding=PurchasedOrderPendingConfirmationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PendingConfirmationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PendingConfirmationAdapter.PendingConfirmationViewHolder,
        position: Int
    ) {
        val order = orders[position]
        val user = users[order.userId]

        with(holder.binding){
            orderId.text= order.id
            userName.text=user?.fullName

            if(user?.imageUser!=null){
                Glide.with(holder.itemView.context).load(user.imageUser)
                    .into(userImage)
            }
            else{
                Glide.with(holder.itemView.context).load(R.drawable.ic_person_circle)
                    .into(userImage)
            }
            checkBox.isChecked=selectedItemIds.contains(order.id)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onItemChecked(order.id, isChecked)
            }

            val layoutManager = LinearLayoutManager(holder.itemView.context)
            orderItemsRec.layoutManager= layoutManager

            val orderItemsAdapter = PurchasedProductAdapter(order.items,products)
            orderItemsRec.adapter=orderItemsAdapter
        }
    }

    override fun getItemCount(): Int = orders.size


    fun updateData(newOrders: List<Order>, newUsers: Map<String, User>, newProducts: Map<String, Product>, newSelectedItemIds: MutableSet<String>) {
        orders = newOrders
        users = newUsers
        products = newProducts
        selectedItemIds = newSelectedItemIds
        notifyDataSetChanged()
    }

    fun updateSelectedItems(selectedOrders: MutableSet<String>) {
        this.selectedItemIds=selectedOrders
        notifyDataSetChanged()
    }
}