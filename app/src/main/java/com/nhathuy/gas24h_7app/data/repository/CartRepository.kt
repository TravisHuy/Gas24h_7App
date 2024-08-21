package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Cart
import com.nhathuy.gas24h_7app.data.model.CartItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepository @Inject constructor(private val db:FirebaseFirestore){

    suspend fun addToCart(userId:String,productId:String,quantity:Int,price:Double):Result<Unit>{
        return try {
            val cartRef =db.collection("carts").document(userId)
            val cartSnapshot = cartRef.get().await()

            if(cartSnapshot.exists()){
                val cart=cartSnapshot.toObject(Cart::class.java)!!
                val existingItem= cart.items.find {
                    it.productId==productId
                }
                if(existingItem!=null){
                    existingItem.quantity+=quantity
                }
                else{
                    cart.items = cart.items + CartItem(productId, quantity, price)
                }
                cart.totalAmount = cart.items.sumByDouble {
                    it.price*it.quantity
                }
                cartRef.set(cart).await()
            }
            else{
                val newCart= Cart(
                    userId=userId,
                    items = listOf(CartItem(productId,quantity, price)),
                    totalAmount = price*quantity
                )
                cartRef.set(newCart).await()
            }

            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
    suspend fun getCartItemCount(userId: String) : Result<Int>{
        return try {
            val cartRef=db.collection("carts").document(userId)
            val cartSnapshot= cartRef.get().await()

            if(cartSnapshot.exists()){
                val cart=cartSnapshot.toObject(Cart::class.java)
                val itemCount = cart?.items?.size ?: 0

                Result.success(itemCount)
            }
            else{
                Result.success(0)
            }
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    suspend fun getCartItemQuantity(userId: String,productId: String): Result<Int>{
        return try {
            val cartRef=db.collection("carts").document(userId)
            val cartSnapshot= cartRef.get().await()

            if(cartSnapshot.exists()){
                val cart=cartSnapshot.toObject(Cart::class.java)
                val quantity =cart?.items?.find { it.productId==productId }?.quantity?:0
                Result.success(quantity)
            }
            else{
                Result.success(0)
            }
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}