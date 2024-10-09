package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ItemAllProductBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class AllProductItemAdapter(private var products:List<Product> = emptyList(),
                            private val onEditClick: (Product) -> Unit,
                            private val onRemoveClick: (Product) -> Unit
) :RecyclerView.Adapter<AllProductItemAdapter.AllProductViewHolder>(){

    inner class AllProductViewHolder(val binding : ItemAllProductBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllProductItemAdapter.AllProductViewHolder {
        val binding = ItemAllProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AllProductItemAdapter.AllProductViewHolder,
        position: Int
    ) {
        val product = products[position]

        with(holder.binding){
            product?.let {
                productName.text = it.name

                val originalPrice = it.price
                val discountedPrice = it.getDiscountedPrice()

                if(product.offerPercentage>0.0){
                    tvProductPercent.text= String.format("-%.0f%%", product.offerPercentage)
                    tvProductPrice.text= NumberFormatUtils.formatPrice(discountedPrice)
                }
                else{
                    tvProductPrice.text = NumberFormatUtils.formatPrice(originalPrice)
                    tvProductPercent.visibility= View.GONE
                }

                Glide.with(holder.itemView.context).load(it.coverImageUrl).into(productImage)

                productSold.text  = holder.binding.root.context.getString(R.string.inventory_count,it.stockCount)

                btnEdit.setOnClickListener { onEditClick(product) }
                btnRemove.setOnClickListener { onRemoveClick(product) }
            }
        }
    }

    override fun getItemCount(): Int  = products.size


    fun updateData(newProducts:List<Product>){
        products = newProducts
        notifyDataSetChanged()
    }
}