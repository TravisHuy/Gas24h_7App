package com.nhathuy.gas24h_7app.ui.chat_message

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ChatAdapter
import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageType
import com.nhathuy.gas24h_7app.databinding.ActivityChatMessageBinding
import com.nhathuy.gas24h_7app.util.Constants.REQUEST_IMAGE_PICK
import javax.inject.Inject

class ChatMessageActivity : AppCompatActivity(),ChatMessageContract.View {

    private lateinit var binding: ActivityChatMessageBinding
    private lateinit var adapter:ChatAdapter
    private var orderId: String? = null
    private var currentUserId : String? = null
    @Inject
    lateinit var presenter: ChatMessagePresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)

        orderId = intent.getStringExtra("EXTRA_ORDER_ID")
        currentUserId = intent.getStringExtra("CURRENT_USER_ID")


        presenter.attachView(this)
        presenter.initialize(orderId)

        setupViews()
        setupListeners()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Send message
        binding.sendButton.setOnClickListener {
            val content = binding.editInputChatMessage.text.toString().trim()
            if (content.isNotBlank()) {
                presenter.sendMessage(content)
            }
        }

        // Image attachment
        binding.attachImageButton.setOnClickListener {
            presenter.onImagePickerClicked()
        }

        // Swipe refresh
        binding.chatMessageSwipeRefreshLayout.setOnRefreshListener {
            presenter.loadMessages()
        }
    }

    private fun setupViews() {

        binding.chatMessageRecyclerviewLayout.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(
            currentUserId = currentUserId!!,
            onMessageClick = { message -> when(message.type) {
                MessageType.IMAGE -> openImageView(message.mediaUrl)
                else -> handleMessageClick(message)
            } },
            onMessageLongClick = { message ->  showMessageOptions(message)},
            onImageClick = { imageUrl -> openImageView(imageUrl) }
        )
        binding.chatMessageRecyclerviewLayout.adapter = adapter
    }


    private fun openImageView(imageUrl: String) {
        val dialogs = Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialogs.setContentView(R.layout.fullscreen_image_layout)

        val fullscreenImage = dialogs.findViewById<ImageView>(R.id.fullscreen_image)
        val closeButton = dialogs.findViewById<ImageView>(R.id.close_button)

        Glide.with(this)
            .load(imageUrl)
            .into(fullscreenImage)

        closeButton.setOnClickListener {
            dialogs.dismiss()
        }

        fullscreenImage.setOnClickListener {
            dialogs.dismiss()
        }
        dialogs.show()
    }

    private fun handleMessageClick(message: Message) {

    }

    private fun showMessageOptions(message: Message) {
        val options = arrayOf("Copy","Delete")
        AlertDialog.Builder(this)
            .setItems(options){
                _,which ->
                when(which){
                    0 -> copyMessageToClipboard(message)
                    1-> deleteMesssage(message)
                }
            }
    }

    private fun copyMessageToClipboard(message: Message) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip  = ClipData.newPlainText("message",message.content)
        clipboard.setPrimaryClip(clip)
    }
    private fun deleteMesssage(message: Message) {

    }

    override fun showLoading() {
        binding.chatMessageSwipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.chatMessageSwipeRefreshLayout.isRefreshing = false
    }

    override fun showMessages(messages: List<Message>) {
        adapter.submitList(messages)
    }

    override fun showMessageSent(message: Message) {
        adapter.submitList(adapter.currentList + message)
        scrollToBottom()
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun scrollToBottom() {
        binding.chatMessageRecyclerviewLayout.scrollToPosition(adapter.itemCount - 1)
    }

    override fun showImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent,REQUEST_IMAGE_PICK)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                presenter.sendImage(uri)
            }
        }
    }
    override fun updateChatRoom(chatRoom: ChatRoom) {
        binding.tvNameShop.text = chatRoom.metadata["title"] as? String ?: "Chat"
    }

    override fun clearInput() {
        binding.editInputChatMessage.text?.clear()
    }

}