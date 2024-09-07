package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Order
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.Exception

class OrderRepository @Inject constructor(private val db:FirebaseFirestore) {

    suspend fun createOrder(order:Order):Result<Unit>{
        return try {
            db.collection("orders").document(order.id).set(order.toMap()).await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

}