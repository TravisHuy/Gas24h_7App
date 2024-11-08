package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ItemChatAdminBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdminItemAdapter(private var rooms:List<ChatRoom> = emptyList(),
                           private val messages:Map<String,Message> = mapOf(),
                           private val users:Map<String,User> = mapOf(),
                           private val onClickChat: (String) -> Unit
):RecyclerView.Adapter<ChatAdminItemAdapter.ChatAdminItemViewHolder>(){

    inner class ChatAdminItemViewHolder(val binding:ItemChatAdminBinding):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                val room = rooms[adapterPosition]
                onClickChat(room.id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatAdminItemAdapter.ChatAdminItemViewHolder {
        val binding = ItemChatAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChatAdminItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChatAdminItemAdapter.ChatAdminItemViewHolder,
        position: Int
    ) {
        val room = rooms[position]
        val message = messages[room.id]
        with(holder.binding){
            message?.let {
                tvDate.text = formatTime(it.timestamp)
                tvChatMessage.text = it.content
                val user = users[message.senderId]
                user?.let {
                    tvUserName.text=user.fullName
                    Glide.with(holder.itemView.context)
                        .load(user.imageUser)
                        .into(productImageUser)
                }
            }

        }
    }

    override fun getItemCount(): Int = rooms.size


    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

}