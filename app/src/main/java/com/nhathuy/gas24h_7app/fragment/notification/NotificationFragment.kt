package com.nhathuy.gas24h_7app.fragment.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.NotificationAdapter
import com.nhathuy.gas24h_7app.data.model.Notification
import com.nhathuy.gas24h_7app.databinding.FragmentChatBinding
import com.nhathuy.gas24h_7app.databinding.FragmentNotificationBinding
import com.nhathuy.gas24h_7app.ui.detail_notification.DetailNotificationActivity
import javax.inject.Inject

class NotificationFragment : Fragment(),NotificationContract.View {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NotificationAdapter

    private val DETAIL_REQUEST_CODE = 1001


    @Inject
    lateinit var presenter:NotificationPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as Gas24h_7Application).getGasComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(layoutInflater,container,false)

        presenter.attachView(this)
        presenter.loadNotifications()
        setupRecyclerview()

        return binding.root
    }

    private fun setupRecyclerview() {
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationAdapter(onItemClicked = {
            notificationId ->
            navigateDetailNotification(notificationId)
        })
        binding.notificationRecyclerView.adapter = adapter
    }

    override fun showLoading() {
        binding.notificationSwipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.notificationSwipeRefreshLayout.isRefreshing = false
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext() , message,Toast.LENGTH_SHORT).show()
    }

    override fun showNotifications(notifications: List<Notification>) {
        adapter.updateData(notifications)
    }

    override fun navigateDetailNotification(id: String) {
        val intent = Intent(requireContext(),DetailNotificationActivity::class.java)
        intent.putExtra("NOTIFICATION_ID",id)
        startActivity(intent)
//        startActivityForResult(intent, DETAIL_REQUEST_CODE)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == DETAIL_REQUEST_CODE) {
//            presenter.loadNotifications()
//        }
//    }
}