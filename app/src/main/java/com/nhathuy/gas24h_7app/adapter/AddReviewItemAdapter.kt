package com.nhathuy.gas24h_7app.adapter

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.ReviewStatus
import com.nhathuy.gas24h_7app.databinding.AddReviewItemBinding
import com.nhathuy.gas24h_7app.ui.add_review.AddReviewContract
import com.nhathuy.gas24h_7app.util.Constants
import java.util.Date
import java.util.UUID

class AddReviewItemAdapter(private val presenter: AddReviewContract.Presenter) :
    ListAdapter<Product, AddReviewItemAdapter.ViewHolder>(ProductDiffCallback()) {
    inner class ViewHolder (val binding : AddReviewItemBinding, presenter: AddReviewContract.Presenter) : RecyclerView.ViewHolder(binding.root){
        private var imageCount = 0
        private var hasVideo = false
        fun bind(product: Product){
            binding.tvProductName.text = product.name
            binding.ratingStart.setOnRatingBarChangeListener { _, rating, _ ->
                presenter.handleRatingChanged(adapterPosition, rating)
                updateRatingDisplay(rating)
            }

            binding.addProductImage.setOnClickListener {
                presenter.handleImageAdded(adapterPosition)
            }

            binding.addProductReviewVideo.setOnClickListener {
                presenter.handleVideoAdded(adapterPosition)
            }

            binding.edComment.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    presenter.validateReviews(getReviews())
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        fun updateImageCount(count: Int) {
            imageCount = count
            binding.addProductTvCount.text = "Thêm hình ảnh ($count/3)"
            binding.addProductImage.isEnabled = count < 3
        }

        fun updateVideoState(hasVideo: Boolean) {
            this.hasVideo = hasVideo
            binding.videoThumbnail.visibility = if (hasVideo) View.VISIBLE else View.GONE
            binding.addProductReviewVideo.isEnabled = !hasVideo
        }

        fun updateRatingDisplay(rating: Float) {
            val reviewStatus = ReviewStatus.fromStars(rating.toInt())
            binding.tvQuality.text = reviewStatus.displayName
        }

        fun getImageCount() = imageCount
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddReviewItemAdapter.ViewHolder {
        val binding = AddReviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding,presenter)
    }

    override fun onBindViewHolder(holder: AddReviewItemAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

}

