package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Voucher
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VoucherRepository @Inject constructor(private val db:FirebaseFirestore) {
    suspend fun createVoucher(voucher: Voucher):Result<Unit>{
        return try {
            db.collection("vouchers").document(voucher.id).set(voucher.toMap()).await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}