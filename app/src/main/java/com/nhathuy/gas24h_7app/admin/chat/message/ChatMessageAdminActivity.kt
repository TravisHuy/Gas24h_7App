package com.nhathuy.gas24h_7app.admin.chat.message

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ChatAdapter
import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.MessageType
import com.nhathuy.gas24h_7app.data.model.UserStatus
import com.nhathuy.gas24h_7app.databinding.ActivityChatMessageAdminBinding
import com.nhathuy.gas24h_7app.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatMessageAdminActivity : AppCompatActivity(),ChatMessageAdminContract.View {

    private lateinit var binding :ActivityChatMessageAdminBinding
    private lateinit var adapter: ChatAdapter
    private var buyerUserId : String? = null
    private var currentAdminId: String? = null
    private var selectedImageUri: Uri? = null
    private var onlineStatusJob: Job? = null

    @Inject
    lateinit var presenter: ChatMessageAdminPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        buyerUserId = intent.getStringExtra("BUYER_ID")
        presenter.attachView(this)
        presenter.initialize(buyerUserId)

        setupViews()
        setupListeners()
        setupImagePreview()
        setupOnlineStatus()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Send message
        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        // Image attachment
        binding.attachImageButton.setOnClickListener {
            presenter.onImagePickerClicked()
        }

        // Swipe refresh
        binding.chatMessageSwipeRefreshLayout.setOnRefreshListener {
            presenter.loadMessages()
        }
        binding.removeImageButton.setOnClickListener {
            clearImagePreview()
        }
    }

    private fun sendMessage() {
        val content = binding.editInputChatMessage.text.toString().trim()

        when {
            selectedImageUri != null -> {
                presenter.sendImage(selectedImageUri!!)
                clearImagePreview()
            }
            content.isNotBlank() -> {
                presenter.sendMessage(content,MessageType.TEXT)
                binding.editInputChatMessage.setText("")
            }
        }
    }

    private fun setupViews() {
        binding.chatMessageRecyclerviewLayout.layoutManager = LinearLayoutManager(this)
    }
    private fun setupImagePreview() {
        binding.imagePreviewLayout.visibility = View.GONE
    }
    private fun clearImagePreview() {
        selectedImageUri = null
        binding.imagePreviewLayout.visibility = View.GONE
        binding.imagePreview.setImageDrawable(null)
    }
    private fun setupOnlineStatus() {
        onlineStatusJob = lifecycleScope.launch {
            buyerUserId?.let { userId ->
                presenter.getUserOnlineStatus(userId).collect { status ->
                    updateOnlineStatus(status)
                }
            }
        }
    }
    override fun updateOnlineStatus(status: UserStatus) {
        binding.apply {
            if (status.isOnline) {
                tvOnline.visibility = View.VISIBLE
                tvMinutes.text = "Online"
            } else {
                tvOnline.visibility = View.GONE
                tvMinutes.text = getTimeAgo(status.lastSeen)
            }
        }
    }

    private fun showImagePreview(uri: Uri) {
        selectedImageUri = uri
        binding.imagePreviewLayout.visibility = View.VISIBLE
        Glide.with(this).load(uri).into(binding.imagePreview)
    }

    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff= now - timestamp
        return when{
            diff < 60_000 -> "just now"
            diff < 3600_000 -> "${diff / 60_000} minutes ago"
            diff < 86400_000 -> "${diff / 3600_000} hours ago"
            else -> "${diff / 86400_000} days ago"
        }
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

    override fun showLoading() {
        binding.chatMessageSwipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.chatMessageSwipeRefreshLayout.isRefreshing = false
    }

    override fun showMessages(messages: List<Message>) {
        adapter.submitList(messages) {
            scrollToBottom()
        }
    }

    override fun showMessageSent(message: Message) {
        adapter.submitList(adapter.currentList + message)
        scrollToBottom()
    }

    override fun showError(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    override fun scrollToBottom() {
        binding.chatMessageRecyclerviewLayout.post {
            binding.chatMessageRecyclerviewLayout.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun showImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, Constants.REQUEST_IMAGE_PICK)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                showImagePreview(uri)
            }
        }
    }



    override fun updateChatRoom(chatRoom: ChatRoom) {
        binding.tvNameUser.text = chatRoom.metadata["title"] as? String ?: "Chat"
    }

    override fun updateCurrentAdminId(adminId: String) {
        this.currentAdminId = adminId
        setupChatAdapter()
    }

    private fun setupChatAdapter() {
        adapter = ChatAdapter(
            currentUserId = currentAdminId!!,
            onMessageClick = { message -> when(message.type) {
                MessageType.IMAGE -> openImageView(message.mediaUrl)
                else -> handleMessageClick(message)
            } },
            onMessageLongClick = { message ->  showMessageOptions(message)},
            onImageClick = { imageUrl -> openImageView(imageUrl) }
        )
        binding.chatMessageRecyclerviewLayout.adapter = adapter
    }

    override fun clearInput() {
        binding.editInputChatMessage.text?.clear()
    }

    override fun updateUserInfo(name: String, avatar: String?) {
        binding.tvNameUser.text = name
    }

}