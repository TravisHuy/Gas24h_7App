package com.nhathuy.gas24h_7app.ui.pending_payment

import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class PendingPaymentPresenter @Inject constructor(private val productRepository: ProductRepository,
                                                  private val cartRepository: CartRepository,
                                                  private val userRepository: UserRepository):PendingPaymentContract.Presenter {

    private var view: PendingPaymentContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: PendingPaymentContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
    }

    override fun loadSuggestProducts() {
        coroutineScope.launch {
            val result = productRepository.getSuggestProductLimitCount(8)
            result.fold(
                onSuccess = {
                    products ->
                    view?.setupSuggestProduct(products)
                },
                onFailure = {
                    e ->
                    view?.showError("${e.message}")
                }
            )
        }
    }

    override fun loadCartItemCount() {
        coroutineScope.launch {
            val userId = userRepository.getCurrentUserId()
            if(userId!=null){
                val result = cartRepository.getCartItemCount(userId)
                result.fold(
                    onSuccess = {count ->
                        view?.updateCartItemCount(count)
                    },
                    onFailure = {e->
                        view?.showError(e.message?:"Failed load cart item count")
                    }
                )
            }
        }
    }
}