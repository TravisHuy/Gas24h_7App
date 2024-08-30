package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Cart
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepository @Inject constructor(private val db:FirebaseFirestore){

    //getcart(userId:String)
    suspend fun getCart(userId: String):Result<Cart>{
        return try {
            val cartDoc= db.collection("carts").document(userId).get().await()
            if(cartDoc.exists()){
                val cart= cartDoc.toObject(Cart::class.java)
                Result.success(cart?:Cart(userId, emptyList(),0.0))
            }
            else{
                Result.success(Cart(userId, emptyList(), 0.0))
            }
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
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
    suspend fun updateCartItemQuantity(userId: String,productId: String,newQuantity:Int):Result<Unit>{
        return try{
            val cartRef = db.collection("carts").document(userId)
            db.runTransaction{
                    transition ->
                val cartSnapshot = transition.get(cartRef)
                val cart = cartSnapshot.toObject(Cart::class.java)
                if(cart!=null){
                    val updateItems= cart.items.map {
                        if(it.productId==productId) it.copy(quantity = newQuantity) else it
                    }
                    transition.update(cartRef,"items",updateItems)
                }
            }.await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
    suspend fun getProductForCart(productIds:List<String>):Result<Map<String,Product>>{
        return try {
            val productSnapshot = db.collection("products").whereIn("id",productIds).get().await()
            val products= productSnapshot.documents.mapNotNull {
                doc ->
                doc.toObject(Product::class.java)?.let {
                    it.id to it
                }
            }.toMap()
            Result.success(products)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    suspend fun removeCartItem(userId: String,productId: String):Result<Unit>{
        return try {
            val cartRef= db.collection("carts").document(userId)
            db.runTransaction {
                transition ->
                val cartSnapshot = transition.get(cartRef)
                val cart= cartSnapshot.toObject(Cart::class.java)
                if(cart!=null){
                    val updateItems= cart.items.filter {
                        it.productId!=productId
                    }
                    transition.update(cartRef,"items",updateItems)
                }
            }.await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
    suspend fun clearCart(userId: String) : Result<Unit>{
        return try {
            val cartRef= db.collection("carts").document(userId)
            cartRef.update("items", emptyList<CartItem>()).await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}