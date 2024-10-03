package com.nhathuy.gas24h_7app.ui.review_of_me

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.NotRatedYetItemAdapter
import com.nhathuy.gas24h_7app.adapter.NotRatedYetListener
import com.nhathuy.gas24h_7app.adapter.ReviewAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityReviewOfMeBinding
import com.nhathuy.gas24h_7app.ui.add_review.AddReviewActivity
import javax.inject.Inject

class ReviewOfMeActivity : AppCompatActivity(), ReviewOfMeContract.View, NotRatedYetListener {

    private lateinit var binding: ActivityReviewOfMeBinding

    private lateinit var notReviewAdapter: NotRatedYetItemAdapter
    private lateinit var reviewAdapter: ReviewAdapter

    @Inject
    lateinit var presenter: ReviewOfMePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewOfMeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupRecyclerView()
        setupTabLayout()
        presenter.loadReviews()
        presenter.loadOrders("DELIVERED")
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        notReviewAdapter = NotRatedYetItemAdapter(listener = this)
        reviewAdapter = ReviewAdapter()
        binding.recyclerView.adapter = notReviewAdapter
    }

    private fun setupTabLayout() {
        binding.tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.recyclerView.adapter = notReviewAdapter
                        presenter.loadOrders("DELIVERED")
                    }

                    1 -> {
                        binding.recyclerView.adapter = reviewAdapter
                        presenter.loadReviews()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    override fun showLoading() {
        binding.loadingTextView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    override fun hideLoading() {
        binding.loadingTextView.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showOrders(orders: List<Order>, products: Map<String, Product>) {
        hideLoading()
        notReviewAdapter.updateOrderProduct(orders, products)
        if (binding.tabLayout.selectedTabPosition == 0) {
            binding.recyclerView.adapter = notReviewAdapter
        }
    }

    override fun showReviews(reviews: List<Review>, users: Map<String, User>) {
        hideLoading()
        reviewAdapter.updateData(reviews, users)
        reviewAdapter.showAllReviews()
        if (binding.tabLayout.selectedTabPosition == 1) {
            binding.recyclerView.adapter = reviewAdapter
        }
    }

    override fun onClick(orderId: String) {
        Log.d("ReviewOfMeActivity", "Order ID passed to AddReviewActivity: $orderId")
        val intent = Intent(this, AddReviewActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        startActivity(intent)
    }

//    override fun onResume() {
//        super.onResume()
//        presenter.attachView(this)
//        refreshData()
//    }
//
//    private fun refreshData() {
//        showLoading()
//        when(binding.tabLayout.selectedTabPosition){
//            0 -> presenter.loadOrders("DELIVERED")
//            1 -> presenter.loadReviews()
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        presenter.detachView()
//    }

}