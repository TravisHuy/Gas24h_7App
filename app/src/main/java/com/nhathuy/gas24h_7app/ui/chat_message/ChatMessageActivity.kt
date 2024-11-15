package com.nhathuy.gas24h_7app.ui.chat_message

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.nhathuy.gas24h_7app.data.model.UserStatus
import com.nhathuy.gas24h_7app.databinding.ActivityChatMessageBinding
import com.nhathuy.gas24h_7app.util.Constants.REQUEST_IMAGE_PICK
import javax.inject.Inject

class ChatMessageActivity : AppCompatActivity(),ChatMessageContract.View {

    private lateinit var binding: ActivityChatMessageBinding
    private lateinit var adapter:ChatAdapter
    private var orderId: String? = null
    private var currentUserId : String? = null
    private var adminId: String? = null
    private var selectedImageUri: Uri? = null
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
        setupImagePreview()
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
        binding.editInputChatMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.sendButton.isEnabled = !s.isNullOrBlank() || selectedImageUri != null
            }
        })
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

    private fun setupImagePreview() {
        binding.imagePreviewLayout.visibility = View.GONE
    }
    private fun clearImagePreview() {
        selectedImageUri = null
        binding.imagePreviewLayout.visibility = View.GONE
        binding.imagePreview.setImageDrawable(null)
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

    override fun showAdminId(adminId: String) {
        this.adminId=adminId
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                showImagePreview(uri)
            }
        }
    }
    private fun showImagePreview(uri: Uri) {
        selectedImageUri = uri
        binding.imagePreviewLayout.visibility = View.VISIBLE
        Glide.with(this).load(uri).into(binding.imagePreview)
    }
    override fun updateChatRoom(chatRoom: ChatRoom) {
        binding.tvNameShop.text = chatRoom.metadata["title"] as? String ?: "Chat"
    }

    override fun clearInput() {
        binding.editInputChatMessage.text?.clear()
    }

    override fun updateOnlineStatus(status: UserStatus) {
        binding.apply {
            if (status.isOnline) {
                tvOnline.visibility = View.VISIBLE
                tvMinutes.text = "Online"
            } else {
                tvOnline.text = "Last seen:"
                tvOnline.visibility = View.GONE
                tvMinutes.text = getTimeAgo(status.lastSeen)
            }
        }
    }
    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000 -> "just now"
            diff < 3600_000 -> "${diff / 60_000} minutes ago"
            diff < 86400_000 -> "${diff / 3600_000} hours ago"
            else -> "${diff / 86400_000} days ago"
        }
    }
    override fun updateParticipantsStatus(statusMap: Map<String, UserStatus>) {
        adminId?.let { userId ->
            val userStatus = statusMap[userId]
            if (userStatus != null) {
                updateOnlineStatus(userStatus)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }
    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }
    override fun onDestroy() {
        presenter.cleanup()
        presenter.detachView()
        super.onDestroy()
    }
}