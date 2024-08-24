package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.VoucherAddProductItemBinding
import com.nhathuy.gas24h_7app.databinding.VoucherProductAllItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoucherProductAdapter(private var products:List<Product> = listOf(),
                            private var selectedItemIds: Set<String> = setOf(),
                            private var onItemChecked:(String,Boolean) -> Unit):RecyclerView.Adapter<VoucherProductAdapter.VoucherProductViewHolder>() {

    inner class VoucherProductViewHolder(val binding:VoucherProductAllItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherProductViewHolder {
        val binding = VoucherProductAllItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VoucherProductViewHolder(binding)
    }

    override fun getItemCount(): Int =products.size

    override fun onBindViewHolder(holder: VoucherProductViewHolder, position: Int) {
        val product=products[position]

        holder.binding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                product?.let {
                    textProductName.text=product.name
                    textProductId.text=product.id
                    textSales.text="${product.reviewCount}"
                    textPrices.text="đ${String.format("%.3f", it.price)}"
                    textStockCount.text="${it.stockCount}"

                    Glide.with(productImage.context)
                        .load(it.coverImageUrl)
                        .into(productImage)
                }
                checkBox.isChecked=selectedItemIds.contains(product.id)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    onItemChecked(product.id, isChecked)
                }
            }
        }
    }
    fun updateProducts(newProducts: List<Product>, newSelectedItemIds: Set<String>) {
        products = newProducts
        selectedItemIds = newSelectedItemIds
        notifyDataSetChanged()
    }

}