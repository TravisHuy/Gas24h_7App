package com.nhathuy.gas24h_7app.ui.detail_product

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailProductPresenter @Inject constructor(private val productRepository: ProductRepository,
                                                private val cartRepository: CartRepository,
                                                private val userRepository: UserRepository):DetailProductContract.Presenter{

    private var view:DetailProductContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun attachView(view: DetailProductContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun loadProductDetails(productId: String) {
        view?.showLoading()
        coroutineScope.launch {
            val result = productRepository.getProductById(productId)
            view?.hideLoading()
            result.fold(
                onSuccess = {
                    product ->
                    view?.showProductDetails(product)
                    view?.setupImageSlider(product.detailImageUrls)
                },
                onFailure = {e->
                    view?.showError("${e.message}")
                }
            )
        }
    }

    override fun loadSuggestProducts(categoryId: String) {
        coroutineScope.launch {
            val result = productRepository.getProductsByCategory(categoryId)
            result.fold(
                onSuccess = {
                    products ->
                            view?.setupSuggestProduct(products)
                },
                onFailure = {e->
                    view?.showError("${e.message}")
                }
            )
        }
    }

    override fun addToCart(productId: String, quantity: Int, price: Double) {
        coroutineScope.launch {
            val userId= userRepository.getCurrentUserId()
            if(userId!=null){
                val result=cartRepository.addToCart(userId, productId, quantity, price)
                result.fold(
                    onSuccess = {view?.showSuccess("Added to cart")},
                    onFailure = {e->view?.showError(e.message?:"Unknown error occured")}
                )
            }
            else{
                view?.showError("Failed add to cart")
            }
        }
    }
}