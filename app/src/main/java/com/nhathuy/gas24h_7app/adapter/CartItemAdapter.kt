package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.CartItemProductBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class CartItemAdapter(private var cartItems:MutableList<CartItem> = mutableListOf(),
                      private var products:Map<String,Product> = mapOf(),
                      private val onQuantityChanged: (String,Int) -> Unit
):RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    @Inject
    lateinit var db:FirebaseFirestore

    inner class CartItemViewHolder(val binding:CartItemProductBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartItemAdapter.CartItemViewHolder {
        val binding = CartItemProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemAdapter.CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val product= products[cartItem.productId]

        holder.binding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                product?.let {
                    cartItemName.text=it.name
                    cartPriceReduce.text="đ${String.format("%.3f", it.getDiscountedPrice())}"
                    cartPriceOriginal.text="đ${String.format("%.3f", it.price)}"

                    Glide.with(cartItemProductImage.context)
                        .load(it.coverImageUrl)
                        .into(cartItemProductImage)
                }

                quantityProductCart.setText(cartItem.quantity.toString())

                decreaseBtn.setOnClickListener {
                    if(cartItem.quantity>1){
                        cartItem.quantity--
                        quantityProductCart.setText(cartItem.quantity.toString())
                        onQuantityChanged(cartItem.productId,cartItem.quantity)
                    }
                }
                increaseBtn.setOnClickListener {
                        cartItem.quantity++
                        quantityProductCart.setText(cartItem.quantity.toString())
                        onQuantityChanged(cartItem.productId,cartItem.quantity)
                }

                quantityProductCart.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus){
                        val newQuantity = quantityProductCart.text.toString().toIntOrNull() ?:1
                        if (newQuantity != cartItem.quantity) {
                            cartItem.quantity = newQuantity
                            quantityProductCart.setText(newQuantity.toString())
                            onQuantityChanged(cartItem.productId, newQuantity)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newCartItems:List<CartItem>, newProducts:Map<String,Product>){
        cartItems.clear()
        cartItems.addAll(newCartItems)
        products=newProducts
        notifyDataSetChanged()
    }
}