package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.databinding.ReviewProductImageBinding

class ReviewImageAdapter(private val images:List<String>):RecyclerView.Adapter<ReviewImageAdapter.ReviewItemViewHolder>() {

    inner class ReviewItemViewHolder(val binding:ReviewProductImageBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewImageAdapter.ReviewItemViewHolder {
        val binding = ReviewProductImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReviewItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewImageAdapter.ReviewItemViewHolder, position: Int) {
        with(holder.binding){
            Glide.with(holder.itemView.context)
                .load(images[position])
                .into(productImage)
        }
    }

    override fun getItemCount(): Int  = images.size
}