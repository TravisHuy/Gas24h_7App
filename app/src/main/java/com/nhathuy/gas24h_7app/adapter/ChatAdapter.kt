package com.nhathuy.gas24h_7app.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageStatus
import com.nhathuy.gas24h_7app.data.model.MessageType
import com.nhathuy.gas24h_7app.util.Constants
import com.nhathuy.gas24h_7app.util.Constants.VIEW_TYPE_RECEIVED_IMAGE
import com.nhathuy.gas24h_7app.util.Constants.VIEW_TYPE_RECEIVED_TEXT
import com.nhathuy.gas24h_7app.util.Constants.VIEW_TYPE_SENT_IMAGE
import com.nhathuy.gas24h_7app.util.Constants.VIEW_TYPE_SENT_TEXT
import com.nhathuy.gas24h_7app.util.Constants.VIEW_TYPE_SYSTEM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.sql.DataSource

class ChatAdapter(private val currentUserId:String,
                  private val onMessageClick :(Message) -> Unit,
                  private val onMessageLongClick : (Message) -> Unit,
                  private val onImageClick : (String) -> Unit
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()){

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when{
            message.type == MessageType.SYSTEM ->Constants.VIEW_TYPE_SYSTEM
            message.senderId == currentUserId && message.type ==MessageType.TEXT ->Constants.VIEW_TYPE_SENT_TEXT
            message.senderId != currentUserId && message.type ==MessageType.TEXT ->Constants.VIEW_TYPE_RECEIVED_TEXT
            message.senderId == currentUserId && message.type ==MessageType.IMAGE ->Constants.VIEW_TYPE_SENT_IMAGE
            message.senderId != currentUserId && message.type ==MessageType.IMAGE ->Constants.VIEW_TYPE_RECEIVED_IMAGE
            else -> throw IllegalArgumentException("Unknown message type")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENT_TEXT -> {
                val view = inflater.inflate(R.layout.item_message_sent, parent, false)
                SentTextMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED_TEXT -> {
                val view = inflater.inflate(R.layout.item_message_received, parent, false)
                ReceivedTextMessageViewHolder(view)
            }
            VIEW_TYPE_SENT_IMAGE -> {
                val view = inflater.inflate(R.layout.item_message_image_sent, parent, false)
                SentImageMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVED_IMAGE -> {
                val view = inflater.inflate(R.layout.item_message_image_received, parent, false)
                ReceivedImageMessageViewHolder(view)
            }
            VIEW_TYPE_SYSTEM -> {
                val view = inflater.inflate(R.layout.item_message_system, parent, false)
                SystemMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message  = getItem(position)
        when(holder){
            is SentTextMessageViewHolder -> holder.bind(message)
            is ReceivedTextMessageViewHolder -> holder.bind(message)
            is SentImageMessageViewHolder -> holder.bind(message)
            is ReceivedImageMessageViewHolder -> holder.bind(message)
            is SystemMessageViewHolder -> holder.bind(message)
            else -> throw IllegalArgumentException("Unknown view holder type")
        }
    }

    inner class SentTextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessageSent)
        private val textTime: TextView = itemView.findViewById(R.id.textTimeSent)

        fun bind(message: Message) {
            textMessage.text = message.content
            textTime.text = formatTime(message.timestamp)

            itemView.setOnLongClickListener {
                onMessageLongClick(message)
                true
            }
        }
    }
    inner class ReceivedTextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessageReceived)
        private val textTime: TextView = itemView.findViewById(R.id.textTimeReceived)

        fun bind(message: Message) {
            textMessage.text = message.content
            textTime.text = formatTime(message.timestamp)

            itemView.setOnLongClickListener {
                onMessageLongClick(message)
                true
            }
        }
    }

    inner class SentImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageMessage: ImageView = itemView.findViewById(R.id.imageMessageSent)
        private val textTime: TextView = itemView.findViewById(R.id.textTimeImageSent)
        private val imageStatus: ImageView = itemView.findViewById(R.id.imageStatusSent)
        private val progressBar: View = itemView.findViewById(R.id.progressBarSent)

        fun bind(message: Message) {
            textTime.text = formatTime(message.timestamp)
            setMessageStatus(message.status, imageStatus)

            // Show loading state if message is sending
            progressBar.visibility = if (message.status == MessageStatus.SENDING) View.VISIBLE else View.GONE

            Glide.with(itemView.context)
                .load(message.mediaUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(imageMessage)

            imageMessage.setOnClickListener { onImageClick(message.mediaUrl) }

            itemView.setOnLongClickListener {
                onMessageLongClick(message)
                true
            }
        }
    }

    inner class ReceivedImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageMessage: ImageView = itemView.findViewById(R.id.imageMessageReceived)
        private val textTime: TextView = itemView.findViewById(R.id.textTimeImageReceived)

        fun bind(message: Message) {
            textTime.text = formatTime(message.timestamp)

            Glide.with(itemView.context)
                .load(message.mediaUrl)
                .into(imageMessage)

            imageMessage.setOnClickListener { onImageClick(message.mediaUrl) }

            itemView.setOnLongClickListener {
                onMessageLongClick(message)
                true
            }
        }
    }

    inner class SystemMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)

        fun bind(message: Message) {
            textMessage.text = message.content
        }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun setMessageStatus(status: MessageStatus, imageView: ImageView) {
        val statusIcon = when (status) {
            MessageStatus.SENDING -> R.drawable.ic_sent
            MessageStatus.SENT -> R.drawable.ic_sent
            MessageStatus.DELIVERED -> R.drawable.ic_sent
            MessageStatus.READ -> R.drawable.ic_sent
            MessageStatus.FAILED -> R.drawable.ic_sent
        }
        imageView.setImageResource(statusIcon)
    }


}
class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}