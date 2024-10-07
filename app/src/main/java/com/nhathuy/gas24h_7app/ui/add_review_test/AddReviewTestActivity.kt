package com.nhathuy.gas24h_7app.ui.add_review_test

import android.app.Activity
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.nhathuy.gas24h_7app.databinding.ActivityAddReviewTestBinding
import com.nhathuy.gas24h_7app.util.Constants
import javax.inject.Inject

class AddReviewTestActivity : AppCompatActivity(),AddReviewTestContract.View{

    private lateinit var binding:ActivityAddReviewTestBinding
    private var orderId: String? = null
    private lateinit var adapter: ProductImageAdapter

    @Inject
    lateinit var presenter:AddReviewTestPresenter

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { presenter.onImageAdded(it) }
        }

    private val videoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { presenter.onVideoAdded(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getStringExtra("ORDER_ID")
        Log.d("AddReviewActivity", "Received Order ID: $orderId")

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupListeners()
        setupRecyclerView()
        setupRatingBar()
        updateImageCount(0, Constants.MAX_IMAGE_COUNT)

        orderId?.let {
            presenter.loadOrder(it)
        } ?: run {
            showMessage("Order ID not found")
            finish()
        }
    }

    private fun setupRatingBar() {
        binding.ratingStart.rating = 5f
        updateReviewStatus(5f)
    }

    private fun setupRecyclerView() {
        adapter = ProductImageAdapter(mutableListOf()) { position ->
            presenter.onImageRemoved(position)
        }
        binding.productImagesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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
        binding.ratingStart.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                updateReviewStatus(rating)
            }
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
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showInformationProduct(product: Product) {
        with(binding) {
            tvProductName.text = product.name
            Glide.with(this@AddReviewTestActivity)
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

    override fun onImageAdded(uri: Uri) {
        adapter.addImage(uri)
        updateImageCount(adapter.itemCount, Constants.MAX_IMAGE_COUNT)
    }

    private fun createVideoThumbnail(videoUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(this, videoUri)
            retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    override fun onVideoAdded(uri: Uri) {
        val thumbnail = createVideoThumbnail(uri)
        if (thumbnail != null) {
            binding.productReviewVideo.visibility = View.GONE
            binding.videoThumbnail.apply {
                visibility = View.VISIBLE
                setImageBitmap(thumbnail)
            }
        } else {
            binding.productReviewVideo.apply {
                visibility = View.VISIBLE
                setVideoURI(uri)
            }
        }
        updateVideoCount(1, 1)
    }

    override fun addImageToAdapter(imageUrl: String) {
        adapter.addImage(Uri.parse(imageUrl))
    }

    override fun removeImageFromAdapter(position: Int) {
        adapter.removeImage(position)
    }

    override fun enableImageAddButton(enable: Boolean) {
        binding.addProductImage.isEnabled = enable
        binding.addProductImage.alpha = if (enable) 1.0f else 0.5f
    }

    override fun enableCoverImageAddButton(enable: Boolean) {
        binding.addProductReviewVideo.isEnabled = enable
        binding.addProductReviewVideo.alpha = if (enable) 1.0f else 0.5f
    }

    override fun clearInputField() {
        binding.edComment.text.clear()
        binding.ratingStart.rating = 0f
    }

    override fun clearImages() {
        adapter.clearImages()
        updateImageCount(0, 3)
    }

    override fun clearVideo() {
        binding.productReviewVideo.apply {
            stopPlayback()
            setVideoURI(null)
            visibility = View.GONE
        }
        binding.videoThumbnail.apply {
            setImageBitmap(null)
            visibility = View.GONE
        }
        updateVideoCount(0, 1)
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