package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Order
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.Exception

class OrderRepository @Inject constructor(private val db:FirebaseFirestore,private  val voucherRepository: VoucherRepository) {

    suspend fun createOrder(order:Order):Result<String>{
        return try {
            val orderRef = db.collection("orders").document()
            val orderId = orderRef.id

            if(order.appliedVoucherId !=null){
                val voucherResult = voucherRepository.updateVoucherUsage(order.appliedVoucherId,order.userId)
                if(voucherResult.isFailure){
                    throw voucherResult.exceptionOrNull() ?: Exception("Failed to update voucher usage")
                }
            }

            orderRef.set(order.copy(id = orderId).toMap()).await()

            Result.success(orderId)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

}