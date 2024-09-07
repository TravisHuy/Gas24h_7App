package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Voucher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    suspend fun getAllVouchers() : Result<List<Voucher>> {
        return withContext(Dispatchers.IO){
            try {
                val query=db.collection("vouchers").get().await()
                val allVoucher=query.documents.mapNotNull {
                    it.toObject(Voucher::class.java)
                }
                Result.success(allVoucher)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    suspend fun getVoucherById(voucherId:String):Result<Voucher>{
        return withContext(Dispatchers.IO){
            try {
                val document = db.collection("vouchers").document(voucherId).get().await()
                val voucher=document.toObject(Voucher::class.java)
                if(voucher!=null){
                    Result.success(voucher)
                }
                else{
                    Result.failure(Exception("Product not found"))
                }
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    suspend fun markVoucherAsUsed(voucherId: String, userId: String): Result<Unit>{
        return withContext(Dispatchers.IO){
            try {
                val voucherRef = db.collection("vouchers").document(voucherId)
                db.runTransaction {
                    transition->
                    val voucherSnapshot = transition.get(voucherRef)
                    val currentUsedBy = voucherSnapshot.get("userBy") as List<String> ?: listOf()
                    transition.update(voucherRef,"userBy",currentUsedBy+userId)
                }.await()
                Result.success(Unit)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
}