package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.BuyBackItemBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.util.NumberFormatUtils

class BuyBackAdapter(private var orders:List<Order> = listOf(),
                     private var products: Map<String, Product> = emptyMap(),
                     private val listener: BuyBackClickListener
):RecyclerView.Adapter<BuyBackAdapter.BuyBackViewHolder>() {

    inner class BuyBackViewHolder(val binding:BuyBackItemBinding):RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(position !=RecyclerView.NO_POSITION){
                    val order = orders[position]
                    val firstItem =order.items.firstOrNull()
                    firstItem?.let {
                        orderItem ->
                        listener.onProductClick(orderItem.productId)
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuyBackAdapter.BuyBackViewHolder {
        val binding = BuyBackItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyBackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyBackAdapter.BuyBackViewHolder, position: Int) {
        val order = orders[position]
        val firstItem = order.items.firstOrNull()

        firstItem?.let { orderItem ->
            val product = products[orderItem.productId]
            with(holder.binding) {
                product?.let {
                    val originalPrice = it.price
                    val discountedPrice = it.getDiscountedPrice()

                    if (it.offerPercentage > 0.0) {
                        tvBuyBackProductPrice.text =
                            NumberFormatUtils.formatPrice(discountedPrice)
                    } else {
                        tvBuyBackProductPrice.text = NumberFormatUtils.formatPrice(originalPrice)

                    }

                    Glide.with(holder.itemView.context).load(product.coverImageUrl)
                        .into(ivProduct)

                    val purchaseCount = calculatePurchaseCount(it.id)
                    tvBuyBackProductPurchaseCount.text = holder.itemView.context.getString(
                        R.string.purchase_count_format,
                        purchaseCount
                    )

                    tvBuyBackProductName.text= it.name
                }
            }
        }
    }

    private fun calculatePurchaseCount(productId: String): Int {
        return orders.count { order ->
            order.items.any {
                it.productId== productId
            }
        }
    }

    override fun getItemCount(): Int = orders.size


    fun updateOrderProducts(newOrders: List<Order>, newProducts: Map<String, Product>) {
        orders = newOrders
        products = newProducts
        notifyDataSetChanged()
    }
    interface BuyBackClickListener {
        fun onProductClick(productId: String)
    }
}