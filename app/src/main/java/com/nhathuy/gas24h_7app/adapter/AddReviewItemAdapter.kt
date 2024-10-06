package com.nhathuy.gas24h_7app.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.ReviewStatus
import com.nhathuy.gas24h_7app.databinding.AddReviewItemBinding
import com.nhathuy.gas24h_7app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddReviewItemAdapter(
    private var reviews: List<Review>,
    private var products: Map<String, Product>,
    private val onImageAdded: (Int) -> Unit,
    private val onImageRemoved: (Int, Int) -> Unit,
    private val onVideoAdded: (Int) -> Unit,
    private val onVideoRemoved: (Int) -> Unit,
    private val onReviewUpdated: (Int, Float, String) -> Unit
) : RecyclerView.Adapter<AddReviewItemAdapter.AddReviewHolder>() {

    var currentFocusedPosition: Int? = null
    private val pendingUpdates = mutableSetOf<Int>()
    private val updateJobs = mutableMapOf<Int, Job>()
    private val adapterScope = CoroutineScope(Dispatchers.Main)

    inner class AddReviewHolder(val binding: AddReviewItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddReviewHolder {
        val binding = AddReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddReviewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddReviewHolder, position: Int) {
        val review = reviews[position]
        val product = products[review.productId]
        with(holder.binding) {
            product?.let {
                tvProductName.text = it.name
                Glide.with(productImage.context)
                    .load(it.coverImageUrl)
                    .into(productImage)
            }
            ratingStart.rating = review.rating
            tvQuality.text = ReviewStatus.fromStars(review.rating.toInt()).displayName
            edComment.setText(review.comment)

            addProductImage.setOnClickListener {
                currentFocusedPosition = position
                onImageAdded(position)
            }

            addProductReviewVideo.setOnClickListener {
                currentFocusedPosition = position
                onVideoAdded(position)
            }

            ratingStart.setOnRatingBarChangeListener { _, rating, _ ->
                updateReviewWithDebounce(position, rating, edComment.text.toString())
            }

            edComment.addTextChangedListener {
                updateReviewWithDebounce(position, ratingStart.rating, it.toString())
            }

            val imageAdapter = AddReviewImageAdapter(
                review.images,
                { onImageRemoved(position, it) }
            )
            productImagesRecyclerView.adapter = imageAdapter
            productImagesRecyclerView.layoutManager = LinearLayoutManager(productImagesRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)

            addProductTvCount.text = "Thêm hình\nảnh(${review.images.size}/${Constants.MAX_IMAGE_COUNT})"

            if (review.video.isNotEmpty()) {
                cardViewCoverImage.visibility = android.view.View.VISIBLE
                Glide.with(productReviewVideo.context)
                    .load(Uri.parse(review.video))
                    .into(videoThumbnail)
                deleteReviewVideo.setOnClickListener {
                    onVideoRemoved(position)
                    cardViewCoverImage.visibility = android.view.View.GONE
                }
            } else {
                cardViewCoverImage.visibility = android.view.View.GONE
            }
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>, newProducts: Map<String, Product>) {
        reviews = newReviews
        products = newProducts
        notifyDataSetChanged()
    }

    fun updateImages(position: Int, images: List<String>) {
        if (position in reviews.indices) {
            reviews = reviews.toMutableList().apply {
                this[position] = this[position].copy(images = images)
            }
            notifyItemChanged(position)
        }
    }

    fun updateVideo(position: Int, video: String) {
        if (position in reviews.indices) {
            reviews = reviews.toMutableList().apply {
                this[position] = this[position].copy(video = video)
            }
            notifyItemChanged(position)
        }
    }

    fun updateRating(position: Int, rating: Float) {
        if (position in reviews.indices) {
            reviews = reviews.toMutableList().apply {
                this[position] = this[position].copy(rating = rating)
            }
            notifyItemChanged(position)
        }
    }

    private fun updateReviewWithDebounce(position: Int, rating: Float, comment: String) {
        updateJobs[position]?.cancel()
        updateJobs[position] = adapterScope.launch {
            delay(300) // Debounce for 300ms
            onReviewUpdated(position, rating, comment)
            notifyItemChanged(position)
        }
    }

    fun notifyItemChangedSafely(position: Int) {
        adapterScope.launch {
            if (position in reviews.indices) {
                notifyItemChanged(position)
            }
        }
    }
    private class ReviewDiffCallback(
        private val oldList: List<Review>,
        private val newList: List<Review>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
    fun notifyPendingUpdates() {
        pendingUpdates.forEach { position ->
            if (position in reviews.indices) {
                notifyItemChanged(position)
            }
        }
        pendingUpdates.clear()
    }
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        updateJobs.values.forEach { it.cancel() }
    }
}