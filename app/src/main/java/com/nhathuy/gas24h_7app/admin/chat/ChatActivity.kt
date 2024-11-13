package com.nhathuy.gas24h_7app.admin.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.adapter.ChatAdminItemAdapter
import com.nhathuy.gas24h_7app.data.model.ChatRoom
import com.nhathuy.gas24h_7app.data.model.Message
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityChatBinding
import javax.inject.Inject

class ChatActivity : AppCompatActivity() ,ChatContract.View{

    private lateinit var binding:ActivityChatBinding
    private lateinit var adapter:ChatAdminItemAdapter

    @Inject
    lateinit var presenter: ChatAdminPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.attachView(this)
        presenter.loadRecentChat()
        setupRecyclerView()
        setupListener()
    }

    private fun setupListener() {
        binding.chatSwipeRefreshLayout.setOnRefreshListener {
            presenter.loadRecentChat()
        }
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdminItemAdapter(onClickChat = {
            chatRoomId ->

        })
        binding.chatRecyclerView.adapter = adapter
    }

    override fun showLoading() {
        binding.chatSwipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.chatSwipeRefreshLayout.isRefreshing = false
    }

    override fun showError(message: String) {
        Log.d("chatactivity","$message")
        val dialog = AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Ok"){
                dialog,_ -> dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    override fun displayRooms(rooms: List<ChatRoom>) {
        adapter.updateRooms(rooms)
    }

    override fun updateChatRoom(messages: Map<String, Message>, users: Map<String, User>) {
        adapter.updateChat(messages,users)
    }

    override fun showEmpty() {
        Toast.makeText(this,"Empty",Toast.LENGTH_SHORT).show()
    }
}