package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val db:FirebaseFirestore){

    suspend fun getCategories(): Result<List<ProductCategory>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("categories").get().await()
            val categories = snapshot.documents.mapNotNull { document ->
                document.toObject(ProductCategory::class.java)
            }.sortedBy { it.categoryName }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateCategory(category: ProductCategory): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("categories").document(category.id).set(category).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryById(categoryId: String): Result<ProductCategory> = withContext(Dispatchers.IO) {
        try {
            val document = db.collection("categories").document(categoryId).get().await()
            val category = document.toObject(ProductCategory::class.java)
            if (category != null) {
                Result.success(category)
            } else {
                Result.failure(Exception("Category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCategory(categoryName: String): Result<ProductCategory> = withContext(Dispatchers.IO) {
        try {
            val highestIdDoc = db.collection("categories")
                .orderBy("id", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get().await()

            val newId = if (!highestIdDoc.isEmpty) {
                val highestId = highestIdDoc.documents[0].getString("id")?.toIntOrNull() ?: 0
                (highestId + 1).toString()
            } else {
                "1"
            }

            val category = ProductCategory(id = newId, categoryName = categoryName)
            db.collection("categories").document(newId).set(category).await()
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            db.collection("categories").document(categoryId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}