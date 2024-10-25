package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ItemAllProductQrScannerBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class QrCodeAdapter(private var products: List<Product>) : RecyclerView.Adapter<QrCodeAdapter.QrCodeViewHolder>(){

    inner class QrCodeViewHolder(val binding:ItemAllProductQrScannerBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QrCodeAdapter.QrCodeViewHolder {
        val binding = ItemAllProductQrScannerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return QrCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QrCodeAdapter.QrCodeViewHolder, position: Int) {
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
            }
        }
    }

    override fun getItemCount(): Int = products.size
}