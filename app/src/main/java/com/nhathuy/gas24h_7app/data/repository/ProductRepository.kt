package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(private val db:FirebaseFirestore) {

    suspend fun getProductById(productId:String):Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val document = db.collection("products").document(productId).get().await()
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    Result.success(product)
                } else {
                    Result.failure(Exception("Product not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

        }
    }
    suspend fun getProductsByCategory(categoryId:String): Result<List<Product>>{
        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("products").whereEqualTo("categoryId",categoryId)
                    .get().await()
                val products = querySnapshot.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }
                Result.success(products)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    suspend fun getProductsByCategoryLimitCount(currentProductId:String,count: Int =6): Result<List<Product>>{

        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("products").get().await()

                val allProduct=querySnapshot.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }

                val filteredProducts = allProduct.filter { it.id!=currentProductId }

                val randomProducts= filteredProducts.shuffled().take(count)

                Result.success(randomProducts)
            }catch (e:Exception){
                Result.failure(e)
            }
        }
    }
    suspend fun getAllProducts() : Result<List<Product>>{
        return withContext(Dispatchers.IO){
            try {
                val querySnapshot=db.collection("products").get().await()
                val allProducts= querySnapshot.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }
                Result.success(allProducts)
            }
            catch (e:Exception){
                Result.failure(e)
            }
        }
    }
}