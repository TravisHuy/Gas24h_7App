package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Exception

class OrderRepository @Inject constructor(private val db:FirebaseFirestore,private  val voucherRepository: VoucherRepository) {

    suspend fun createOrder(order:Order):Result<String>{
        return try {
            val orderRef = db.collection("orders").document()
            val orderId = orderRef.id

            orderRef.set(order.copy(id = orderId).toMap()).await()

            Result.success(orderId)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
    suspend fun getOrders(status:String) : Result<List<Order>>{
        return withContext(Dispatchers.IO){
            try {
                val snapshot = db.collection("orders").whereEqualTo("status",status).get().await()

                val orders = snapshot.documents.mapNotNull {
                    it.toObject(Order::class.java)
                }

                Result.success(orders)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    suspend fun getOrdersForUser(userId:String,status:String) : Result<List<Order>>{
        return withContext(Dispatchers.IO){
            try {
                val snapshot = db.collection("orders")
                    .whereEqualTo("userId",userId)
                    .whereEqualTo("status",status)
                    .get().await()

                val orders = snapshot.documents.mapNotNull {
                    it.toObject(Order::class.java)
                }

                Result.success(orders)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    suspend fun updateOrderStatus(orderId:String,newStatus:OrderStatus): Result<Unit>{
        return withContext(Dispatchers.IO){
            try {
                db.collection("orders").document(orderId)
                    .update("status",newStatus.name)
                    .await()
                Result.success(Unit)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    suspend fun getOrderId(orderId: String) : Result<Order>{
        return withContext(Dispatchers.IO){
            try {
                val snapshot= db.collection("orders").document(orderId).get().await()
                val order = snapshot.toObject(Order::class.java)
                if(order!=null){
                    Result.success(order)
                }
                else {
                    Result.failure(Exception("Failed to convert snapshot to Order"))
                }
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
}