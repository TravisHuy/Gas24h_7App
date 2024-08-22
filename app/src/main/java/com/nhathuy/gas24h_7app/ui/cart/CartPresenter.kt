package com.nhathuy.gas24h_7app.ui.cart

import android.util.Log
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

    private var cartItems: List<CartItem> = emptyList()
    private var products : Map<String,Product> = emptyMap()
    private val selectItemIds = mutableSetOf<String>()
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
                Log.d("CartPresenter", "Loading cart for user: $userId")
                val cartDocument =db.collection("carts").document(userId!!).get().await()
                Log.d("CartPresenter", "Cart document exists: ${cartDocument.exists()}")
                val cart=cartDocument.toObject(Cart::class.java)
                Log.d("CartPresenter", "Cart object: ${cart}")

                if(cart!=null){
                    val productIds= cart.items.map {
                        it.productId
                    }
                    Log.d("CartPresenter", "Product IDs to load: $productIds")
                    val productSnapshot =db.collection("products").whereIn("id",productIds).get().await()
                    Log.d("CartPresenter", "Number of products loaded: ${productSnapshot.size()}")
                    products= productSnapshot.documents.mapNotNull {
                        doc ->
                        doc.toObject(Product::class.java)?.let {
                            Log.d("CartPresenter", "Loaded product: ${it.id} - ${it.name}")
                            it.id to it
                        }
                    }.toMap()
                    cartItems =cart.items
                    view?.showCartItems(cart.items,products)
                    view?.showCartSize(cartItems.size)
                    calculateTotalPrice()
                }
                else{
                    Log.d("CartPresenter", "Cart is empty or null")
                    view?.showCartItems(emptyList(), emptyMap())
                }
            }
            catch (e:Exception){
                Log.e("CartPresenter", "Error loading cart items", e)
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

    override fun updateItemSelection(productId: String, isChecked: Boolean) {
        if(isChecked){
            selectItemIds.add(productId)
        }
        else{
            selectItemIds.remove(productId)
        }
        calculateTotalPrice()
    }

    override fun calculateTotalPrice() {
        val total =cartItems.filter { it.productId in selectItemIds }.sumOf { cartItem ->
            val product= products[cartItem.productId]
            product?.let {
                if(it.offerPercentage>0.0){
                    (it.getDiscountedPrice())*cartItem.quantity
                }
                else{
                    it.price*cartItem.quantity
                }
            }?:0.0
        }
        Log.d("CartPresenter", "Total price calculated: $total")
        view?.updateTotalPrice(total)
        view?.updatePurchaseBtnText(selectItemIds.size)
    }

    override fun selectAllItems(isChecked: Boolean) {
        if(isChecked){
            selectItemIds.addAll(cartItems.map { it.productId })
        }
        else{
            selectItemIds.clear()
        }
        view?.updateSelectedItems(selectItemIds)
        calculateTotalPrice()
    }

    override fun updateAllItemsSelection(isChecked: Boolean) {
        selectAllItems(isChecked)
        view?.updateAllItemsSelection(isChecked)
    }

    override fun handleManualQuantityExceeded(productId: String, maxQuantity: Int) {
        view?.showStockExceededError(productId, maxQuantity)
        updateCartItemQuantity(productId, maxQuantity)
    }

    override fun onBtnClicked() {
        val selectItems = cartItems.filter { it.productId in selectItemIds }
        val totalAmount =calculateTotalAmount(selectItems)
        view?.navigateToCheckout(selectItems, totalAmount)
    }

    private fun calculateTotalAmount(items: List<CartItem>): Double {
        return items.sumOf {
            cartItem ->
            val product=products[cartItem.productId]
            product?.let {
                if(it.offerPercentage>0.0){
                    (it.getDiscountedPrice())*cartItem.quantity
                }
                else{
                    it.price*cartItem.quantity
                }
            }?:0.0
        }
    }


}