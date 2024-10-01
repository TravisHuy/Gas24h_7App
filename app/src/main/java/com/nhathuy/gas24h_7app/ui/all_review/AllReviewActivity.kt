package com.nhathuy.gas24h_7app.ui.all_review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ReviewAdapter
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityAllReviewBinding
import javax.inject.Inject

class AllReviewActivity : AppCompatActivity(),AllReviewContract.View {

    private lateinit var binding:ActivityAllReviewBinding
    private lateinit var adapter: ReviewAdapter
    @Inject
    lateinit var presenter:AllReviewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val productId = intent.getStringExtra("PRODUCT_ID") ?: ""

        setupRecyclerView()
        presenter.loadReviews(productId)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter()
        binding.recyclerView.adapter = adapter
    }

    override fun showLoading() {
        binding.loadingTextView.visibility= View.VISIBLE
        binding.contentScrollView.visibility= View.GONE
    }

    override fun hideLoading() {
        binding.loadingTextView.visibility=View.GONE
        binding.contentScrollView.visibility=View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this , message , Toast.LENGTH_SHORT).show()
    }

    override fun showReviews(reviews: List<Review>, users: Map<String, User>) {
        adapter.updateData(reviews,users)
        adapter.showAllReviews()
        val averageRating = reviews.map { it.rating }.average().toFloat()
        binding.ratingProductStar.rating = averageRating
        binding.tvNumberStars.text = String.format("%.1f/5", averageRating)
    }
}