package com.nhathuy.gas24h_7app.admin.print_invoice.print_invoice_detail

import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class PrintVoiceDetailPresenter @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : PrintVoiceDetailContract.Presenter {

    private var view: PrintVoiceDetailContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private var productMap = mapOf<String, Product>()
    private var userMap = mapOf<String, User>()


    override fun attachView(view: PrintVoiceDetailContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun printVoices(orderIds: List<String>) {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = orderRepository.getListOrders(orderIds)
                result.fold(
                    onSuccess = { orders ->
                        val productIds =
                            orders.flatMap { it.items.map { item -> item.productId } }.distinct()

                        productMap = productIds.mapNotNull { productId ->
                            val productResult = productRepository.getProductById(productId)
                            productResult.getOrNull()?.let { productId to it }
                        }.toMap()

                        userMap = orders.mapNotNull { order ->
                            val userResult = userRepository.getUser(order.userId)
                            userResult.getOrNull()?.let { order.userId to it }
                        }.toMap()


                        view?.showOrders(orders, productMap, userMap)
                    },
                    onFailure = { e ->
                        view?.showError("Failed load print order: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showError("Failed to load print orders: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

}