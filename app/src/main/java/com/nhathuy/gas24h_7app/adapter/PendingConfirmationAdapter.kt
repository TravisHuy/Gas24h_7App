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
                                 private var onItemChecked:(String,Boolean) -> Unit,
                                 private var onItemClicked : (String) -> Unit
):RecyclerView.Adapter<PendingConfirmationAdapter.PendingConfirmationViewHolder>() {

    inner class PendingConfirmationViewHolder(val binding:PurchasedOrderPendingConfirmationItemBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val order =orders[adapterPosition]
                toggleCheckbox(order.id)
            }
            binding.checkBox.setOnClickListener {
                val order = orders[adapterPosition]
                onItemChecked(order.id, binding.checkBox.isChecked)
            }
        }
        private fun toggleCheckbox(orderId: String) {
            binding.checkBox.isChecked = !binding.checkBox.isChecked
            onItemChecked(orderId, binding.checkBox.isChecked)
        }
    }


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

            if (user?.imageUser != null && user.imageUser.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(user.imageUser)
                    .placeholder(R.drawable.ic_person_circle)
                    .error(R.drawable.ic_person_circle)
                    .into(userImage)
            } else {
                userImage.setImageResource(R.drawable.ic_person_circle)
            }


            checkBox.isChecked=selectedItemIds.contains(order.id)
            checkBox.setOnCheckedChangeListener(null)

            val layoutManager = LinearLayoutManager(holder.itemView.context)
            orderItemsRec.layoutManager= layoutManager

            val orderItemsAdapter = PurchasedProductAdapter(order.items,products){
                toogleCheckbox(order.id)
            }
            orderItemsRec.adapter=orderItemsAdapter
        }
    }

    private fun toogleCheckbox(orderId: String) {
        val newSelectedItemIds = selectedItemIds.toMutableSet()

        if(newSelectedItemIds.contains(orderId)){
            newSelectedItemIds.remove(orderId)
        }
        else{
            newSelectedItemIds.add(orderId)
        }
        selectedItemIds=newSelectedItemIds
        onItemChecked(orderId,newSelectedItemIds.contains(orderId))
        notifyDataSetChanged()
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

    fun clearSelection() {
        selectedItemIds= emptySet()
        notifyDataSetChanged()
    }
}