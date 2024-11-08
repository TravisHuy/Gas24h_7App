package com.nhathuy.gas24h_7app.admin.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() ,ChatContract.View{

    private lateinit var binding:ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }
}