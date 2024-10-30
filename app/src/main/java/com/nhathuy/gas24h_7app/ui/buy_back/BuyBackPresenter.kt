package com.nhathuy.gas24h_7app.ui.buy_back

import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.CartRepository
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class BuyBackPresenter @Inject constructor(private val orderRepository: OrderRepository,
                                           private val userRepository: UserRepository,
                                           private val productRepository: ProductRepository,
                                           private val cartRepository: CartRepository
):BuyBackContract.Presenter {

    private var view: BuyBackContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    override fun attachView(view: BuyBackContract.View) {
        this.view=view
    }

    override fun detachView() {
        view= null
    }

    override fun loadOrders() {
        coroutineScope.launch {

            try {
                val userId = userRepository.getCurrentUserId()

                val result = orderRepository.getOrdersForUserBuyBack(userId!!)
                view?.showLoading()
                result.fold(
                    onSuccess = { orders ->
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

                        // Gộp các đơn hàng theo productId
                        val consolidatedOrders = orders.flatMap { order ->
                            order.items.map { item ->
                                item.productId to order
                            }
                        }.groupBy(
                            { it.first }, // group by productId
                            { it.second } // keep the full order
                        ).map { (productId, ordersList) ->
                            // Tạo đơn hàng mới với thông tin tổng hợp
                            val firstOrder = ordersList.first()
                            val totalQuantity = ordersList.sumOf { order ->
                                order.items.filter { it.productId == productId }.sumOf { it.quantity }
                            }

                            Order(
                                id = firstOrder.id,
                                userId = firstOrder.userId,
                                items = listOf(firstOrder.items.first { it.productId == productId }.copy(quantity = totalQuantity)),
                                totalAmount = firstOrder.totalAmount,
                                discountAmount = firstOrder.discountAmount,
                                appliedVoucherId = firstOrder.appliedVoucherId,
                                status = firstOrder.status,
                                createdAt = firstOrder.createdAt,
                                updatedAt = firstOrder.updatedAt,
                                shippingAddress = firstOrder.shippingAddress,
                                paymentMethod = firstOrder.paymentMethod
                            )
                        }

                        view?.showOrders(consolidatedOrders, productMap)
                    },
                    onFailure = { e ->
                        view?.showError("Failed to load order: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showError("Failed to load order: ${e.message}")
            } finally {
                view?.hideLoading()
            }

        }
    }

    override fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        coroutineScope.launch {
            try {
                val result = orderRepository.updateOrderStatus(orderId, newStatus)
                result.fold(
                    onSuccess = {
                        loadOrders()
                    },
                    onFailure = { e->
                        view?.showError("Failed to update order status: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showError("Failed to update order status: ${e.message}")
            }
        }
    }

    override fun loadCartItemCount() {
        coroutineScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                if(userId!=null){
                    val result = cartRepository.getCartItemCount(userId)
                    result.fold(
                        onSuccess = { count ->
                            view?.updateCartItemCount(count)
                        },
                        onFailure = { e->
                            view?.showError(e.message?:"Failed to load cart item count")
                        }
                    )
                }
            }
            catch (e:Exception){
                view?.showError(e.message?:"Failed to load cart item count")
            }
        }
    }
}