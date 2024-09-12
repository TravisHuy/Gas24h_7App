package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Voucher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class VoucherRepository @Inject constructor(private val db: FirebaseFirestore) {
    suspend fun createVoucher(voucher: Voucher): Result<Unit> {
        return try {
            db.collection("vouchers").document(voucher.id).set(voucher.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllVouchers(): Result<List<Voucher>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = db.collection("vouchers").get().await()
                val allVoucher = query.documents.mapNotNull {
                    it.toObject(Voucher::class.java)
                }
                Result.success(allVoucher)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getVoucherById(voucherId: String): Result<Voucher> {
        return withContext(Dispatchers.IO) {
            try {
                val document = db.collection("vouchers").document(voucherId).get().await()
//                val voucher = document.toObject(Voucher::class.java)?.copy(
//                    isForAllProducts = document.getBoolean("isForAllProducts") ?: false,
//                    applicableProductIds = document.get("applicableProductIds") as? List<String>
//                        ?: listOf()
//                )

                val voucher = document.toObject(Voucher::class.java)

                if (voucher != null) {
                    Result.success(voucher)
                } else {
                    Result.failure(Exception("Product not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateVoucherUsage(
        voucherId: String,
        userId: String
    ): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                var remainingUsages = 0
                db.runTransaction { transaction ->
                    val voucherRef = db.collection("vouchers").document(voucherId)
                    val voucherSnapshot = transaction.get(voucherRef)
                    val voucher = voucherSnapshot.toObject(Voucher::class.java)
                        ?: throw Exception("Voucher not found")

                    if (!voucher.isValidForUser(userId)) {
                        throw Exception("Voucher is not valid for this user")
                    }

                    val userUsages = voucher.userUsages.toMutableMap()
                    val userUsageCount = userUsages[userId] ?: 0
                    userUsages[userId] = userUsageCount +1

                    val newCurrentUsage = voucher.currentUsage

                    if (newCurrentUsage > voucher.maxUsage) {
                        throw Exception("Voucher usage limit exceeded")
                    }

                    transaction.update(voucherRef,
                        "currentUsage", voucher.currentUsage + 1,
                        "userUsages", userUsages
                    )

                    userUsages[userId] ?: 0
                }.await()

                Result.success(1)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun getApplicableVouchersForProducts(productIds: List<String>): Result<List<Voucher>> {
        return withContext(Dispatchers.IO) {
            try {
                val allVoucher = getAllVouchers().getOrThrow()
                val applicableVouchers = allVoucher.filter { voucher ->
                    voucher.isApplicableToProducts(productIds) && voucher.isActive &&
                            voucher.currentUsage < voucher.maxUsage && Date() in voucher.startDate..voucher.endDate
                }
                Result.success(applicableVouchers)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}