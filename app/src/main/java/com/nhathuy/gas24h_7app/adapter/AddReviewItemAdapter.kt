package com.nhathuy.gas24h_7app.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.ReviewStatus
import com.nhathuy.gas24h_7app.databinding.AddReviewItemBinding
import com.nhathuy.gas24h_7app.util.Constants
import java.util.Date
import java.util.UUID
class AddReviewItemAdapter(private val onReviewChanged: (Int, Float, String) -> Unit,
                           private val onAddImage: (Int) -> Unit,
                           private val onRemoveImage: (Int, Int) -> Unit,
                           private val onAddVideo: (Int) -> Unit,
                           private val onRemoveVideo: (Int) -> Unit
):RecyclerView.Adapter<AddReviewItemAdapter.AddReviewViewHolder>() {

    private val products = mutableListOf<Product>()
    private val reviews = mutableListOf<Review>()

    inner class AddReviewViewHolder(val binding:AddReviewItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product, review: Review, position: Int) {
            binding.tvProductName.text = product.name
            Glide.with(itemView.context).load(product.coverImageUrl).into(binding.productImage)
            binding.ratingStart.rating = review.rating
            binding.edComment.setText(review.comment)

            binding.ratingStart.setOnRatingBarChangeListener { _, rating, _ ->
                onReviewChanged(position, rating, binding.edComment.text.toString())
                updateQualityText(rating)
            }
            updateImageCount(review.images.size)

            binding.edComment.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    onReviewChanged(position, binding.ratingStart.rating, binding.edComment.text.toString())
                }
            }

            binding.addProductImage.setOnClickListener { onAddImage(position) }
            binding.addProductReviewVideo.setOnClickListener { onAddVideo(position) }

            updateImageAddButton(review.images.size < Constants.MAX_IMAGES)
            updateVideoAddButton(review.video.isEmpty())

            // Set up image recycler view
            setupImageRecycler(review.images,position)
            // Set up video thumbnail
            setupVideoThumbnail(review.video, position)

        }
        private fun updateImageCount(count: Int) {
            binding.addProductTvCount.text = "Thêm hình\nảnh($count/3)"
        }
        private fun updateQualityText(rating: Float) {
            val qualityText = when (rating.toInt()) {
                1 -> "Tệ"
                2 -> "Kém"
                3 -> "Bình thường"
                4 -> "Tốt"
                5 -> "Xuất sắc"
                else -> ""
            }
            binding.tvQuality.text = qualityText
        }

        fun updateImageAddButton(enabled: Boolean) {
            binding.addProductImage.isEnabled = enabled
            binding.addProductImage.alpha = if (enabled) 1.0f else 0.5f
        }

        fun updateVideoAddButton(enabled: Boolean) {
            binding.addProductReviewVideo.isEnabled = enabled
            binding.addProductReviewVideo.alpha = if (enabled) 1.0f else 0.5f
        }

        private fun setupImageRecycler(images: List<String>, position: Int) {
            val imageAdapter = ProductImageAdapter(images.map { Uri.parse(it) }.toMutableList()) { index ->
                onRemoveImage(position, index)
            }
            binding.productImagesRecyclerView.apply {
                adapter = imageAdapter
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            }
        }
        private fun setupVideoThumbnail(videoUri: String, position: Int) {
            if (videoUri.isNotEmpty()) {
                binding.videoThumbnail.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(videoUri)
                    .into(binding.videoThumbnail)
                binding.deleteReviewVideo.setOnClickListener { onRemoveVideo(position) }
            } else {
                binding.videoThumbnail.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddReviewItemAdapter.AddReviewViewHolder {
        val binding = AddReviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddReviewViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AddReviewViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if(payloads.isEmpty()){
            onBindViewHolder(holder,position)
        }
        else{
            val payload = payloads[0]
            when(payload){
                "UPDATE_IMAGE_BUTTON" -> holder.updateImageAddButton(reviews[position].images.size < Constants.MAX_IMAGES)
                "UPDATE_VIDEO_BUTTON" -> holder.updateVideoAddButton(reviews[position].video.isEmpty())
            }
        }
    }
    override fun onBindViewHolder(holder: AddReviewItemAdapter.AddReviewViewHolder, position: Int) {
        holder.bind(products[position],reviews[position],position)
    }

    override fun getItemCount(): Int = products.size

    fun setProducts(newProducts: List<Product>){
        products.clear()
        products.addAll(newProducts)
        reviews.clear()
        reviews.addAll(List(newProducts.size){ Review() })
        notifyDataSetChanged()
    }

    fun updateReview(position: Int, updatedReview: Review) {
        reviews[position] = updatedReview
        notifyItemChanged(position)
    }

    fun updateImageAddButton(position: Int, enabled: Boolean) {
        notifyItemChanged(position, "UPDATE_IMAGE_BUTTON")
    }

    fun updateVideoAddButton(position: Int, enabled: Boolean) {
        notifyItemChanged(position, "UPDATE_VIDEO_BUTTON")
    }

}