package com.nhathuy.gas24h_7app.ui.order_information

import com.nhathuy.gas24h_7app.data.model.OrderStatus
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

class OrderInformationPresenter @Inject constructor(private val userRepository: UserRepository,
                                                    private val orderRepository: OrderRepository,
                                                    private val productRepository: ProductRepository
):OrderInformationContract.Presenter {

    private var view:OrderInformationContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun attachView(view: OrderInformationContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun loadUserInfo() {
        coroutineScope.launch {
            val result = userRepository.getUser(userRepository.getCurrentUserId()!!)
            result.fold(
                onSuccess = {user->
                    view?.showUserInfo(user)
                },
                onFailure = {e->
                    view?.showError("Failed load user ${e.message}")
                }
            )
        }
    }

    override fun loadOrder(orderId: String) {
        coroutineScope.launch {
            try {
                val result = orderRepository.getOrderId(orderId)

                result.fold(
                    onSuccess = { order ->
                        val productIds = order.items.map { it.productId }
                        val productMap= mutableMapOf<String, Product>()
                        val productJobs= productIds.map {
                                productId->
                            async(Dispatchers.IO){
                                val productResult = productRepository.getProductById(productId)
                                productResult.getOrNull()?.let {
                                        product -> productMap[productId] = product
                                }
                            }
                        }
                        productJobs.forEach { it.await() }
                        view?.showOrder(order,productMap)
                    },
                    onFailure = {e->
                        view?.showError("Failed to load order: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Failed load order ${e.message}")
            }
        }
    }

    override fun loadSuggestProducts() {
        coroutineScope.launch {
            val result = productRepository.getSuggestProductLimitCount(8)
            result.fold(
                onSuccess = { products->
                    view?.setupSuggestProduct(products)
                },
                onFailure = {e->
                    view?.showError("Failed load suggest products ${e.message}")
                }
            )
        }
    }

    override fun cancelOrder(orderId: String) {
        coroutineScope.launch {
            try {
                val result = orderRepository.updateOrderStatus(orderId,OrderStatus.CANCELLED)
                result.fold(
                    onSuccess = {
                        loadOrder(orderId)
                        view?.navigatePurchase()
                        view?.showMessage("Cancel order successfully")
                    },
                    onFailure = {e->
                        view?.showError("Can't cancel order ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Can't cancel order ${e.message}")
            }
        }
    }

}