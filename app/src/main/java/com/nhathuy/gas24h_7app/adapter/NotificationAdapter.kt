package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.data.model.Notification
import com.nhathuy.gas24h_7app.databinding.ItemNotificationBinding

class NotificationAdapter(private var notifications:List<Notification> = emptyList(),
                          private val onItemClicked: (String) -> Unit
):RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>(){
    inner class NotificationViewHolder(val binding : ItemNotificationBinding):RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val notification = notifications[adapterPosition]
                onItemClicked(notification.id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationAdapter.NotificationViewHolder,
        position: Int
    ) {
        val notification = notifications[position]
        with(holder.binding){
            notification?.let {
                notificationTitle.text = it.title
            }
        }
    }

    override fun getItemCount(): Int = notifications.size


    fun updateData(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

}