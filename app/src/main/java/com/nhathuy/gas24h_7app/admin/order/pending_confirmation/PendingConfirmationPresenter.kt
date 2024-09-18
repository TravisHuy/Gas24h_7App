package com.nhathuy.gas24h_7app.admin.order.pending_confirmation

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.OrderRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class PendingConfirmationPresenter @Inject constructor(private val orderRepository: OrderRepository,
                                                       private val productRepository: ProductRepository,
                                                       private val userRepository: UserRepository
): PendingConfirmationContract.Presenter{

    private  var view:PendingConfirmationContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)


    private val selectedOrders = mutableSetOf<String>()
    private var allOrders = listOf<Order>()

    private var isAllSelected =false

    override fun attachView(view: PendingConfirmationContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadOrders(status: String) {
        coroutineScope.launch {
            val result = orderRepository.getOrders(status)

            result.fold(
                onSuccess = {
                        orders ->
                    //update allOrders
                    allOrders=orders
                    val productIds= orders.flatMap { it.items.map { item-> item .productId} }.distinct()
                    val productMap= mutableMapOf<String, Product>()
                    val userMap = mutableMapOf<String, User>()

                    //load information product
                    val productJobs= productIds.map {
                            productId->
                        async(Dispatchers.IO){
                            val productResult = productRepository.getProductById(productId)
                            productResult.getOrNull()?.let {
                                    product -> productMap[productId] = product
                            }
                        }
                    }

                    //load information user
                    val userJobs = orders.map { order ->
                        async(Dispatchers.IO) {
                            val userResult = userRepository.getUser(order.userId)
                            userResult.getOrNull()?.let { 
                                user -> userMap[order.userId]= user
                            }
                        }
                    }


                    productJobs.forEach { it.await() }
                    userJobs.forEach { it.await() }

                    view?.showOrders(orders,productMap,userMap)
                },
                onFailure = {e->
                    view?.showError("Failed to load order: ${e.message}")
                }
            )
        }
    }

    override fun getSelectedOrders(): List<Order> {
        return allOrders.filter {
            it.id in selectedOrders
        }
    }

    override fun updateItemSelection(orderId: String, isChecked: Boolean) {
        if (isChecked){
            selectedOrders.add(orderId)
        }
        else{
            selectedOrders.remove(orderId)
        }
        isAllSelected = selectedOrders.size == allOrders.size
        view?.updateSelectAllCheckbox(isAllSelected)
    }

    override fun toggleSelectAll(isChecked: Boolean) {
        isAllSelected = isChecked
        if (isChecked) {
            selectedOrders.addAll(allOrders.map { it.id })
        } else {
            selectedOrders.clear()
        }
        view?.updateOrderList(allOrders, selectedOrders)
    }

}