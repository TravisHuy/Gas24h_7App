    package com.nhathuy.gas24h_7app.adapter

    import android.net.Uri
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.nhathuy.gas24h_7app.R

    class AddReviewImageAdapter(
        private val images: List<String>,
        private val onImageRemoved: (Int) -> Unit
    ) : RecyclerView.Adapter<AddReviewImageAdapter.ImageViewHolder>() {


        class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val productImage: ImageView = view.findViewById(R.id.product_image)
            val deleteImage: ImageView = view.findViewById(R.id.delete_image)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.add_image_item, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val imageUri = images[position]
            Glide.with(holder.itemView.context)
                .load(imageUri)
                .into(holder.productImage)
            holder.deleteImage.setOnClickListener {
                onImageRemoved(position)
            }
        }

        override fun getItemCount(): Int = images.size

    }