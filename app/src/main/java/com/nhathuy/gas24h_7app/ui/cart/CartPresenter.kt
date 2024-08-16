package com.nhathuy.gas24h_7app.ui.cart

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Cart
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartPresenter @Inject constructor(private val db:FirebaseFirestore, private val userRepository: UserRepository):CartContract.Presenter{

    private var view:CartContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: CartContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun loadCartItems() {
        coroutineScope.launch {
            try {
                val userId= userRepository.getCurrentUserId()
                val cartDocument =db.collection("carts").document(userId!!).get().await()
                val cart=cartDocument.toObject(Cart::class.java)

                if(cart!=null){
                    val productIds= cart.items.map {
                        it.productId
                    }
                    val productSnapshot =db.collection("products").whereIn("id",productIds).get().await()
                    val products= productSnapshot.documents.mapNotNull {
                        doc ->
                        doc.toObject(Product::class.java)?.let {
                            it.id to it
                        }
                    }.toMap()
                    view?.showCartItems(cart.items,products)
                }
                else{
                    view?.showCartItems(emptyList(), emptyMap())
                }
            }
            catch (e:Exception){
                view?.showError("Failed to load cart items: ${e.message}")
            }
        }
    }


    override fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                val cartRef = db.collection("carts").document(userId!!)

                db.runTransaction { transaction ->
                    val cartSnapshot = transaction.get(cartRef)
                    val cart = cartSnapshot.toObject(Cart::class.java)
                    if (cart != null) {
                        val updatedItems = cart.items.map {
                            if (it.productId == productId) it.copy(quantity = newQuantity) else it
                        }
                        transaction.update(cartRef, "items", updatedItems)
                    }
                }.await()

                view?.updateCartItemQuantity(productId, newQuantity)
            } catch (e: Exception) {
                view?.showError("Failed to update quantity: ${e.message}")
            }
        }
    }


}