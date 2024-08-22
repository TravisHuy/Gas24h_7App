package com.nhathuy.gas24h_7app.ui.order

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.ui.cart.CartContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderPresenter @Inject constructor(private val userRepository: UserRepository,
                                         private val productRepository: ProductRepository):OrderContract.Presenter{
    private var view:OrderContract.View? = null
    private var orderItems= mutableListOf<CartItem>()
    private val products = mutableMapOf<String,Product>()

    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun attachView(view: OrderContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
        job.cancel() // Cancel the coroutine scope when detaching the view
    }

    override fun loadOrderItems(selectedItems: MutableList<CartItem>) {
        orderItems = selectedItems // Update the orderItems list
        coroutineScope.launch {
            selectedItems.forEach { cartItem ->
                val productResult = productRepository.getProductById(cartItem.productId)
                if (productResult.isSuccess) {
                    products[cartItem.productId] = productResult.getOrNull()!!
                } else {
                    view?.showError("Failed to load product: ${cartItem.productId}")
                }
            }
            view?.showOrderItems(orderItems, products)
        }
    }


}