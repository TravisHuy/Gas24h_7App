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
                           private var messages:Map<String,Message> = mapOf(),
                           private val currentAdminId: String,
                           private val onClickBuyerId: (String) -> Unit
):RecyclerView.Adapter<ChatAdminItemAdapter.ChatAdminItemViewHolder>(){

    private var users = mutableMapOf<String, User>()

    inner class ChatAdminItemViewHolder(val binding:ItemChatAdminBinding):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val room = rooms[position]
                    val buyerId = room.participants.find { it != currentAdminId }
                    buyerId?.let { id ->
                        onClickBuyerId(id)
                    }
                }
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
        val buyerId = room.participants.find { it != currentAdminId }

        with(holder.binding){
            message?.let {
                tvDate.text = formatTime(it.timestamp)
                tvChatMessage.text = it.content
            }
            buyerId?.let { id->
                users[id]?.let { buyer ->
                    tvUserName.text = buyer.fullName
                    Glide.with(holder.itemView.context)
                        .load(buyer.imageUser)
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

    fun updateRooms(newRooms: List<ChatRoom>) {
        rooms = newRooms
        notifyDataSetChanged()
    }

    fun updateChat(newMessages: Map<String, Message>, newUsers: Map<String, User>) {
        messages = newMessages
        users.putAll(newUsers)
        notifyDataSetChanged()
    }

}