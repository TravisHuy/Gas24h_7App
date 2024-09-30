package com.nhathuy.gas24h_7app.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ReviewProductBinding
import com.nhathuy.gas24h_7app.databinding.ReviewProductItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(private val reviews:List<Review> = listOf(),
                    private val users:Map<String,User> = mapOf()
):RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(val binding:ReviewProductItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewAdapter.ReviewViewHolder {
        val binding = ReviewProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ReviewViewHolder, position: Int) {
        val review= reviews[position]
        val user = users[review.userId]

        with(holder.binding){
            user?.let {
                tvUserName.text=it.fullName
                Glide.with(holder.itemView.context).load(it.imageUser)
                    .error(R.drawable.ic_person_circle)
                    .into(profileImage)
            }
            review?.let {
                tvComment.text=it.comment
                ratingBar.rating=it.rating

                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(it.date)

                tvReviewDate.text = formattedDate

                if(it.video.isNotEmpty()){
                    relativeLayoutVideo.visibility=View.VISIBLE

                    val videoUri = Uri.parse(it.video)

                    reviewVideo.setVideoURI(videoUri)
                    reviewVideo.setOnPreparedListener {
                        mp ->
                        reviewVideo.start()

                        val duration = mp.duration
                        val durationInSeconds =duration/1000
                        val minutes = durationInSeconds/60
                        val seconds = durationInSeconds % 60

                        videoDuration.text = String.format("%d:%02d", minutes, seconds)
                    }
                    reviewVideo.setOnCompletionListener {
                        reviewVideo.start()
                    }
                }
                else{
                    relativeLayoutVideo.visibility=View.GONE
                }

                // image
                if(it.images.isNotEmpty()){
                    recyclerReviewImages.visibility=View.VISIBLE
                    recyclerReviewImages.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
                    recyclerReviewImages.adapter = ReviewImageAdapter(review.images)
                }

            }
        }
    }

    override fun getItemCount(): Int = reviews.size

}