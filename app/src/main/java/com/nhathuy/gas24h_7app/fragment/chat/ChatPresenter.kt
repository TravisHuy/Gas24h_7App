package com.nhathuy.gas24h_7app.fragment.chat

import android.util.Log
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatPresenter @Inject constructor(private val orderRepository: OrderRepository,
                                        private val userRepository: UserRepository,
                                        private val productRepository: ProductRepository
): ChatContract.Presenter {

    private var view:ChatContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: ChatContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadOrders() {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                val result = orderRepository.getOrderAll(userId!!)

                result.fold(
                    onSuccess = { orders->
                        val productIds =
                            orders.flatMap { it.items.map { item -> item.productId } }.distinct()
                        val productMap = mutableMapOf<String, Product>()
                        val productJobs = productIds.map { productId ->
                            async(Dispatchers.IO) {
                                val productResult = productRepository.getProductById(productId)
                                productResult.getOrNull()?.let { product ->
                                    productMap[productId] = product
                                }
                            }
                        }
                        productJobs.forEach { it.await() }
                        view?.showOrders(orders, productMap)
                    },
                    onFailure = {e->
                        Log.d("ChatPresenter","Failed load orders :${e.message}")
                    }
                )
            }
            catch (e:Exception){
                Log.d("ChatPresenter","Failed load orders :${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }
}