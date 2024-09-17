package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Order
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
}