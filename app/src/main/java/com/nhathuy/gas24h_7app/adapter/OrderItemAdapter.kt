package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.OrderItemBinding

class OrderItemAdapter(private var cartItems:MutableList<CartItem> = mutableListOf(),
                       private var products: MutableMap<String,Product> = mutableMapOf()):RecyclerView.Adapter<OrderItemAdapter.OrderViewHolder>() {
    inner class OrderViewHolder(val binding:OrderItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderItemAdapter.OrderViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemAdapter.OrderViewHolder, position: Int) {
        val cartItem=  cartItems[position]
        val product = products[cartItem.productId]

        with(holder.binding){
            product?.let {
                orderItemName.text=it.name
                orderProductPrice.text=if(it.offerPercentage>0){
                    "đ${String.format("%,.3f", it.getDiscountedPrice())}"
                }
                else{
                    "đ${String.format("%,.3f", it.price)}"
                }
                orderCountItem.text="x${cartItem.quantity}"
                orderCountProduct.text=holder.binding.root.context.getString(R.string.count_product,cartItem.quantity)

                val totalPrice= cartItem.quantity * (if (it.offerPercentage > 0) it.getDiscountedPrice() else it.price)

                productTotalPrice.text="đ${String.format("%,.3f", totalPrice)}"


                Glide.with(orderItemProductImage.context)
                    .load(it.coverImageUrl)
                    .into(orderItemProductImage)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newCartItems:MutableList<CartItem>, newProducts:MutableMap<String,Product>){
        cartItems=newCartItems
        products=newProducts
        notifyDataSetChanged()
    }
}