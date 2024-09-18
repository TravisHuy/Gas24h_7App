package com.nhathuy.gas24h_7app.admin.order.pending_confirmation

import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
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

    private var filteredOrders = listOf<Order>()
    private var productMap = mapOf<String,Product>()
    private var userMap = mapOf<String,User>()
    override fun attachView(view: PendingConfirmationContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadOrders(status: String) {

        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = orderRepository.getOrders(status)
                result.fold(
                    onSuccess = {
                            orders ->
                        //update allOrders
                        allOrders=orders
                        val productIds= orders.flatMap { it.items.map { item-> item .productId} }.distinct()

                        productMap = productIds.mapNotNull { productId ->
                            val productResult = productRepository.getProductById(productId)
                            productResult.getOrNull()?.let { productId to it }
                        }.toMap()

                        userMap = orders.mapNotNull { order ->
                            val userResult = userRepository.getUser(order.userId)
                            userResult.getOrNull()?.let { order.userId to it }
                        }.toMap()


                        view?.showOrders(orders,productMap,userMap)
                    },
                    onFailure = {e->
                        view?.showError("Failed to load order: ${e.message}")
                    })
            }
            catch (e:Exception){
                view?.showError("Failed to load orders: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }

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

    override fun searchOrders(query: String) {
        val lowercaseQuery = query.toLowerCase()
        filteredOrders = allOrders.filter { 
            order ->
            val user = userMap[order.userId]
            user?.fullName?.toLowerCase()?.contains(lowercaseQuery) == true
        }
        view?.showOrders(filteredOrders, productMap, userMap)
    }

    override fun confirmSelectOrders() {
        val selectedOrderIds = selectedOrders.toList()

        coroutineScope.launch {
            view?.showLoading()
           try {
               selectedOrderIds.map {
                       orderId -> orderRepository.updateOrderStatus(orderId,OrderStatus.PROCESSING)
                   allOrders = allOrders.filter {
                       it.id != orderId
                   }
                   selectedOrders.remove(orderId)
               }
               view?.showOrders(allOrders,productMap,userMap)
               view?.updateOrderList(allOrders,selectedOrders)
               view?.showMessage("Selected orders have been confirmed")
           }
           catch (e:Exception){
               view?.showError("Failed to confirm orders: ${e.message}")
           }
           finally {
               view?.hideLoading()
           }
        }
    }

    override fun clearSelection() {
        selectedOrders.clear()
        isAllSelected=false
        view?.clearSelectItems()
        view?.updateSelectAllCheckbox(false)
    }

}