package com.nhathuy.gas24h_7app.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReviewImageAdapter(private val images:List<String>):RecyclerView.Adapter<ReviewImageAdapter.ReviewItemViewHolder>() {

    inner class ReviewItemViewHolder(val imageView:androidx.appcompat.widget.AppCompatImageView):RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewImageAdapter.ReviewItemViewHolder {
        val imageView = androidx.appcompat.widget.AppCompatImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(100,90)
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        }
        return ReviewItemViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ReviewImageAdapter.ReviewItemViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(images[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int  = images.size
}