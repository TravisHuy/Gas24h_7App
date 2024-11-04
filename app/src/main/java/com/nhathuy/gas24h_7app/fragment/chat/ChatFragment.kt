package com.nhathuy.gas24h_7app.fragment.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.BuyBackItemAdapter
import com.nhathuy.gas24h_7app.adapter.ChatOrderItemAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.FragmentChatBinding
import com.nhathuy.gas24h_7app.databinding.FragmentProfileBinding
import com.nhathuy.gas24h_7app.ui.chat_message.ChatMessageActivity
import javax.inject.Inject

class ChatFragment : Fragment(),ChatContract.View {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ChatOrderItemAdapter

    @Inject
    lateinit var presenter:ChatPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as Gas24h_7Application).getGasComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentChatBinding.inflate(inflater,container,false)

        presenter.attachView(this)
        setupRecyclerview()

        presenter.loadOrders()
        setupListeners()

        Log.d("ChatFragment",presenter.getCurrentUserId()!!)
        return binding.root
    }

    private fun setupListeners() {
        binding.chatSwipeRefreshLayout.setOnRefreshListener {
            presenter.loadOrders()
        }
    }

    private fun setupRecyclerview() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatOrderItemAdapter(onItemClicked = {
            orderId ->
            navigateChatMessage(orderId)
        })
        binding.chatRecyclerView.adapter = adapter
    }

    private fun navigateChatMessage(orderId: String) {
        val intent  = Intent(requireContext(),ChatMessageActivity::class.java)
        intent.putExtra("EXTRA_ORDER_ID",orderId)
        intent.putExtra("CURRENT_USER_ID",presenter.getCurrentUserId())
        startActivity(intent)
    }

    override fun showLoading() {
        binding.chatSwipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.chatSwipeRefreshLayout.isRefreshing = false
    }

    override fun showOrders(orders: List<Order>, products: Map<String, Product>) {
        adapter.updateDate(orders,products)
    }


}