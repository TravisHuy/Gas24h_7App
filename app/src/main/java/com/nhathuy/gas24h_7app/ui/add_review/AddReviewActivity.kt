
package com.nhathuy.gas24h_7app.ui.add_review

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductImageAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ReviewStatus
import com.nhathuy.gas24h_7app.databinding.ActivityAddReviewBinding
import javax.inject.Inject

class AddReviewActivity : AppCompatActivity(),AddReviewContract.View {
    private lateinit var binding:ActivityAddReviewBinding
    private var orderId: String? = null
    private lateinit var adapter: ProductImageAdapter
    @Inject
    lateinit var presenter: AddReviewPresenter

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { presenter.onImageAdded(it) }
    }

    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { presenter.onVideoAdded(it) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId=intent.getStringExtra("ORDER_ID")

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupListeners()
        setupRecyclerView()

        orderId?.let {
            presenter.loadOrder(it)
        } ?:run {
            showMessage("Order ID not found")
            finish()
        }
    }
    private fun setupRecyclerView() {
        adapter = ProductImageAdapter(mutableListOf()) { position ->
            presenter.onImageRemoved(position)
        }
        binding.productImagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.productImagesRecyclerView.adapter = adapter
    }
    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            val rating = binding.ratingStart.rating
            val comment = binding.edComment.text.toString()
            presenter.submitReview(rating, comment)
        }
        binding.addProductImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.addProductReviewVideo.setOnClickListener {
            videoPickerLauncher.launch("video/*")
        }
        binding.deleteReviewVideo.setOnClickListener {
            presenter.onVideoRemoved()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.ratingStart.setOnRatingBarChangeListener { _, rating, _ ->
            updateReviewStatus(rating)
        }
    }
    private fun updateReviewStatus(rating: Float) {
        val reviewStatus = ReviewStatus.fromStars(rating.toInt())
        binding.tvQuality.text = reviewStatus.displayName
        binding.tvQuality.setTextColor(getColorForReviewStatus(reviewStatus))
    }

    private fun getColorForReviewStatus(status: ReviewStatus): Int {
        return when (status) {
            ReviewStatus.ONE_STAR, ReviewStatus.TWO_STARS -> getColor(android.R.color.holo_red_light)
            ReviewStatus.THREE_STARS -> getColor(android.R.color.holo_orange_light)
            ReviewStatus.FOUR_STARS, ReviewStatus.FIVE_STARS -> getColor(android.R.color.holo_green_light)
        }
    }
    override fun showLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility=View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showInformationProduct(product: Product) {
        with(binding) {
            tvProductName.text = product.name
            Glide.with(this@AddReviewActivity)
                .load(product.coverImageUrl)
                .into(productImage)
        }
    }

    override fun updateImageCount(count: Int, max: Int) {
        binding.addProductTvCount.text = "Thêm hình\nảnh($count/$max)"
    }

    override fun updateVideoCount(count: Int, max: Int) {
        if (count > 0) {
            binding.cardViewCoverImage.visibility = View.VISIBLE
            binding.addProductReviewVideo.isEnabled = false
        } else {
            binding.cardViewCoverImage.visibility = View.GONE
            binding.addProductReviewVideo.isEnabled = true
        }
    }

    override fun clearInputField() {
        binding.edComment.text.clear()
        binding.ratingStart.rating = 0f
    }

    override fun clearImages() {
       updateImageCount(0,3)
    }

    override fun clearVideo() {
        binding.productReviewVideo.stopPlayback()
        binding.productReviewVideo.setVideoURI(null)
        updateVideoCount(0, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
