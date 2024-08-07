package com.nhathuy.gas24h_7app.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R

class ProductImageAdapter(
    private val images: MutableList<Uri>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val deleteImage: ImageView = view.findViewById(R.id.delete_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUri)
            .into(holder.productImage)
        holder.deleteImage.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
    fun addImage(uri: Uri){
        images.add(uri)
        notifyItemInserted(images.size-1)
    }
    fun removeImage(position: Int){
        if(position in images.indices){
            images.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,images.size)
        }
    }
    fun clearImages(){
        val size=images.size
        images.clear()
        notifyItemRangeRemoved(0, size)
    }
}