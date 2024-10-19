package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.util.splitQueryIntoKeyWords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(private val db:FirebaseFirestore) {

    suspend fun searchProducts(query:String):Result<List<Product>> = withContext(Dispatchers.IO){
        try {
            val keywords= splitQueryIntoKeyWords(query)
            // truy vấn bằng cách sử dụng từng từ khóa

            val querySnapshot = db.collection("products").orderBy("name").get().await()

            //lọc các sản phẩm mà tên hoặc mô tả chứa tất cả từ khóa
            val filteredProducts= querySnapshot.documents.mapNotNull {
                val product = it.toObject(Product::class.java)
                product?.let {
                    if(keywords.all { keyword ->
                            product.name.contains(keyword,true) || product.description.contains(keyword,true)
                        }){
                        product
                    }
                    else{
                        null
                    }
                }
            }

            Result.success(filteredProducts)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
    suspend fun getRecentSearches(): Result<List<Product>> = withContext(Dispatchers.IO){
        Result.success(emptyList())
    }
    suspend fun saveRecentSearch(query:String): Result<Unit> = withContext(Dispatchers.IO){
        Result.success(Unit)
    }
}