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

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentImagePosition?.let { position ->
                    presenter.onImageAdded(
                        position,
                        uri
                    )
                }
            }
        }

    private val videoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentVideoPosition?.let { position ->
                    presenter.onVideoAdded(
                        position,
                        uri
                    )
                }
            }
        }
    private var currentImagePosition: Int? = null
    private var currentVideoPosition: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        intent.getStringExtra("ORDER_ID")?.let {
            presenter.loadOrder(it)
        } ?: run {
            showMessage("Order ID not found")
            finish()
        }

        setupListeners()
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        adapter = AddReviewItemAdapter(
            onReviewChanged = { position, rating, comment ->
                presenter.updateReview(position, rating, comment)
            },
            onAddImage = { position ->
                currentImagePosition = position
                imagePickerLauncher.launch("image/*")
            },
            onRemoveImage = { position, imagePosition ->
                presenter.onImageRemoved(position, imagePosition)
            },
            onAddVideo = { position ->
                currentVideoPosition = position
                videoPickerLauncher.launch("video/*")
            },
            onRemoveVideo = { position ->
                presenter.onVideoRemoved(position)
            }
        )

        binding.productsRecyclerView.adapter = adapter
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            presenter.submitReviews()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
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

    override fun updateProductList(products: List<Product>) {
        adapter.setProducts(products)
    }

    override fun updateReviewUI(position: Int, review: Review) {
        adapter.updateReview(position,review)
    }

    override fun updateImageAddButton(position: Int, enabled: Boolean) {
        adapter.updateImageAddButton(position, enabled)
    }

    override fun updateVideoAddButton(position: Int, enabled: Boolean) {
        adapter.updateVideoAddButton(position, enabled)
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
