package com.nhathuy.gas24h_7app.ui.add_review

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.adapter.AddReviewItemAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.databinding.ActivityAddReviewBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddReviewActivity : AppCompatActivity(), AddReviewContract.View {

    private lateinit var binding: ActivityAddReviewBinding
    private lateinit var adapter: AddReviewItemAdapter

    @Inject
    lateinit var presenter: AddReviewPresenter

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            adapter.currentFocusedPosition?.let { position ->
                presenter.onImageAdded(position, selectedUri)
            }
        }
    }

    private val videoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            adapter.currentFocusedPosition?.let { position ->
                presenter.onVideoAdded(position, selectedUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupRecyclerView()
        setupListeners()

        val orderId = intent.getStringExtra("ORDER_ID")
        orderId?.let {
            presenter.loadOrder(it)
        } ?: run {
            showMessage("Order ID not found")
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = AddReviewItemAdapter(
            emptyList(),
            emptyMap(),
            { position -> imagePicker.launch("image/*") },
            { reviewIndex, position -> presenter.onImageRemoved(reviewIndex, position) },
            { position -> videoPicker.launch("video/*") },
            { reviewIndex -> presenter.onVideoRemoved(reviewIndex) },
            { reviewIndex, rating, comment -> presenter.updateReview(reviewIndex, rating, comment) }
        )
        binding.productsRecyclerView.adapter = adapter
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.productsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    adapter.notifyPendingUpdates()
                }
            }
        })
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            presenter.submitReviews()
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.cancelButton.setOnClickListener {
            showCancelConfirmationDialog()
        }
    }

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hủy đánh giá")
            .setMessage("Bạn có chắc chắn muốn hủy đánh giá không?")
            .setPositiveButton("Có") { _, _ -> finish() }
            .setNegativeButton("Không", null)
            .show()
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

    override fun updateAdapter(reviews: List<Review>, products: Map<String, Product>) {
        lifecycleScope.launch {
            adapter.updateData(reviews, products)
        }
    }

    override fun updateImages(position: Int, images: List<String>) {
        adapter.updateImages(position, images)
    }

    override fun updateVideo(position: Int, video: String) {
        adapter.updateVideo(position, video)
    }

    override fun updateRating(position: Int, rating: Float) {
        adapter.updateRating(position, rating)
    }

    override fun notifyItemChanged(position: Int) {
        adapter.notifyItemChangedSafely(position)
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
        showCancelConfirmationDialog()
    }
}