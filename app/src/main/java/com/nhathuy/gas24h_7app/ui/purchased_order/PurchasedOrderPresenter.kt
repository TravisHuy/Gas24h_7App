package com.nhathuy.gas24h_7app.ui.purchased_order

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class PurchasedOrderPresenter @Inject constructor(private val orderRepository: OrderRepository,
                                                  private  val productRepository: ProductRepository):PurchasedOrderContract.Presenter{

    private var view:PurchasedOrderContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun attachView(view: PurchasedOrderContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun loadOrders(status: String) {
        coroutineScope.launch {
            val result = orderRepository.getOrders(status)

            result.fold(
                onSuccess = {
                    orders ->
                    val productIds= orders.flatMap { it.items.map { item-> item .productId} }.distinct()
                    val productMap= mutableMapOf<String,Product>()
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
                    view?.showOrders(orders,productMap)
                },
                onFailure = {e->
                    view?.showError("Failed to load order: ${e.message}")
                }
            )
        }
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

}