package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val db:FirebaseFirestore){

    suspend fun getCategories() : Result<List<ProductCategory>>{
        return try{
            val snapshot = db.collection("categories").get().await()
            val categories = snapshot.documents.mapNotNull { document ->
                document.data?.let { ProductCategory.fromMap(it) }
            }.sorted()
            Result.success(categories)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

}