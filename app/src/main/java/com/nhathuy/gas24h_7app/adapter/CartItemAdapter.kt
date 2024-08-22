package com.nhathuy.gas24h_7app.adapter

import android.util.Log
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
                      private var selectedItemIds:Set<String> = setOf(),
                      private val onQuantityChanged: (String,Int) -> Unit,
                      private val onQuantityExceeded: (String, Int) -> Unit,
                      private val onManualQuantityExceeded: (String,Int) -> Unit,
                      private val onItemChecked:(String,Boolean) -> Unit
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

                    if(it.offerPercentage > 0.0){
                        cartPriceReduce.text="đ${String.format("%.3f", it.getDiscountedPrice())}"
                        cartPriceOriginal.text="đ${String.format("%.3f", it.price)}"
                        cartPriceOriginal.visibility=View.VISIBLE
                    }
                    else{
                        cartPriceReduce.text="đ${String.format("%.3f", it.price)}"
                        cartPriceOriginal.visibility=View.GONE
                    }

                    Log.d("CartItemAdapter", "Product: ${it.name}, Price: ${it.price}, OfferPercentage: ${it.offerPercentage}")

                    Glide.with(cartItemProductImage.context)
                        .load(it.coverImageUrl)
                        .into(cartItemProductImage)
                }
                checkBox.isChecked = selectedItemIds.contains(cartItem.productId)
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    onItemChecked(cartItem.productId, isChecked)
                }

                quantityProductCart.setText(cartItem.quantity.toString())
                quantityProductCart.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        val newQuantity = quantityProductCart.text.toString().toIntOrNull() ?: 1
                        val maxQuantity = product?.stockCount ?: 0

                        when {
                            newQuantity > maxQuantity -> {
                                cartItem.quantity = maxQuantity
                                quantityProductCart.setText(maxQuantity.toString())
                                onManualQuantityExceeded(cartItem.productId, maxQuantity)
                            }
                            newQuantity != cartItem.quantity -> {
                                cartItem.quantity = newQuantity
                                quantityProductCart.setText(newQuantity.toString())
                                onQuantityChanged(cartItem.productId, newQuantity)
                            }
                        }
                    }
                }
                decreaseBtn.setOnClickListener {
                    if(cartItem.quantity>1){
                        cartItem.quantity--
                        quantityProductCart.setText(cartItem.quantity.toString())
                        onQuantityChanged(cartItem.productId,cartItem.quantity)
                    }
                }
                increaseBtn.setOnClickListener {
                        if(product!=null && cartItem.quantity<product.stockCount){
                            cartItem.quantity++
                            quantityProductCart.setText(cartItem.quantity.toString())
                            onQuantityChanged(cartItem.productId,cartItem.quantity)
                        }
                        else{
                            onQuantityExceeded(cartItem.productId,product?.stockCount?:0)
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
    fun updateSelectedItems(selectedItemIds: Set<String>){
        this.selectedItemIds=selectedItemIds
        notifyDataSetChanged()
    }
}