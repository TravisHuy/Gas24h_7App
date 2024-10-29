package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Exception

class OrderRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val voucherRepository: VoucherRepository
) {

    //tạo order
    suspend fun createOrder(order: Order): Result<String> {
        return withContext(Dispatchers.IO){
            try {
               val orderId = db.runTransaction {
                    transaction->
                    val orderRef = db.collection("orders").document()
                    val newOrderId = orderRef.id

                   val productRefs  = order.items.map { item ->
                       db.collection("products").document(item.productId)
                   }
                   val productSnapshots = productRefs.map { ref ->
                       transaction.get(ref)
                   }

                   val productUpdates = order.items.mapIndexed { index, item ->
                       val snapshot = productSnapshots[index]
                       val product = snapshot.toObject(Product::class.java) ?: throw  Exception("Product not found: ${item.productId}")
                       if(product.soldCount<item.quantity){
                           throw Exception("Insufficient stock product ${product.name}")
                       }

                       Pair(productRefs[index],product.stockCount - item.quantity)
                   }

                   productUpdates.forEach { (ref,newCount) ->
                       transaction.update(ref,"stockCount",newCount)
                   }
//                    order.items.forEach {
//                        item->
//                        val productRef = db.collection("products").document(item.productId)
//                        val productSnapshot = transition.get(productRef)
//                        val product = productSnapshot.toObject(Product::class.java) ?:throw Exception("Product not found :${item.productId}")
//
//                        if(product.stockCount<item.quantity){
//                            throw Exception("Insufficient stock for product ${product.name}")
//                        }
//
//                        val newStockCount = product.stockCount  -item.quantity
//                        transition.update(productRef,"stockCount",newStockCount)
//                    }

                   transaction.set(orderRef,order.copy(id = newOrderId).toMap())

                    newOrderId
                }.await()

                Result.success(orderId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //lấy ra danh sách order theo status
    suspend fun getOrders(status: String): Result<List<Order>> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection("orders").whereEqualTo("status", status).get().await()

                val orders = snapshot.documents.mapNotNull {
                    it.toObject(Order::class.java)
                }

                Result.success(orders)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //lấy ra danh sách order đã đặt của người dùng
    suspend fun getOrdersForUser(userId: String, status: String): Result<List<Order>> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection("orders")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", status)
                    .get().await()

                val orders = snapshot.documents.mapNotNull {
                    it.toObject(Order::class.java)
                }

                Result.success(orders)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //cập nhập lại trạng thái của order
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                db.runTransaction { transaction ->
                    val orderRef = db.collection("orders").document(orderId)
                    val orderSnapshot = transaction.get(orderRef)
                    val order = orderSnapshot.toObject(Order::class.java)
                        ?: throw Exception("Order not found")

                    if (order.status != newStatus) {
                        // Đọc tất cả dữ liệu sản phẩm trước
                        val productSnapshots = order.items.map { item ->
                            val productRef = db.collection("products").document(item.productId)
                            transaction.get(productRef)
                        }

                        // Cập nhật trạng thái đơn hàng
                        transaction.update(orderRef, "status", newStatus.name)

                        // Cập nhật soldCount cho các sản phẩm nếu cần
                        if (newStatus == OrderStatus.DELIVERED) {
                            order.items.forEachIndexed { index, item ->
                                val productSnapshot = productSnapshots[index]
                                val currentSoldCount = productSnapshot.getLong("soldCount") ?: 0
                                val updatedSoldCount = currentSoldCount + item.quantity
                                transaction.update(productSnapshot.reference, "soldCount", updatedSoldCount)
                            }
                        }
                    }
                }.await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(Exception("Failed to update order status: ${e.message}"))
            }
        }
    }


    //lấy order the id
    suspend fun getOrderId(orderId: String): Result<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection("orders").document(orderId).get().await()
                val order = snapshot.toObject(Order::class.java)
                if (order != null) {
                    Result.success(order)
                } else {
                    Result.failure(Exception("Failed to convert snapshot to Order"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // lấy ra số lượng của order the trạng thái của order
    suspend fun getOrderCountForUser(userId: String, status: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.collection("orders")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("status", status)
                    .get()
                    .await()
                Result.success(snapshot.count())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}