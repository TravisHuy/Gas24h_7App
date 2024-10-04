package com.nhathuy.gas24h_7app.ui.add_review

import android.app.Activity
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.AddReviewItemAdapter
import com.nhathuy.gas24h_7app.adapter.ProductImageAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.ReviewStatus
import com.nhathuy.gas24h_7app.databinding.ActivityAddReviewBinding
import com.nhathuy.gas24h_7app.util.Constants
import javax.inject.Inject

class AddReviewActivity : AppCompatActivity(), AddReviewContract.View {
    private lateinit var binding: ActivityAddReviewBinding
    private lateinit var adapter: AddReviewItemAdapter

    @Inject
    lateinit var presenter: AddReviewPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val orderId = intent.getStringExtra("ORDER_ID") ?: ""
        presenter.loadProductsFromOrder(orderId)

        setupListeners()
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        adapter = AddReviewItemAdapter(presenter)
        binding.productsRecyclerView.adapter = adapter
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        val reviews = adapter.getReviews()
        presenter.submitReviews(reviews)
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showProducts(products: List<Product>) {
        adapter.submitList(products)
    }

    override fun onReviewSubmitted() {
        Toast.makeText(this, "Reviews submitted successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun updateImageCount(position: Int, count: Int) {
        (binding.productsRecyclerView.findViewHolderForAdapterPosition(position) as? AddReviewItemAdapter.ViewHolder)?.updateImageCount(count)
    }

    override fun updateVideoState(position: Int, hasVideo: Boolean) {
        (binding.productsRecyclerView.findViewHolderForAdapterPosition(position) as? AddReviewItemAdapter.ViewHolder)?.updateVideoState(hasVideo)
    }

    override fun updateRatingDisplay(position: Int, rating: Float) {
        (binding.productsRecyclerView.findViewHolderForAdapterPosition(position) as? AddReviewItemAdapter.ViewHolder)?.updateRatingDisplay(rating)
    }

    override fun enableSubmitButton(enable: Boolean) {
        binding.btnSend.isEnabled = enable
    }


    override fun navigateBack() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
