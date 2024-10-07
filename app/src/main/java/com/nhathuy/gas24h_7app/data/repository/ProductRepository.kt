package com.nhathuy.gas24h_7app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
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

    suspend fun getSuggestProductLimitCount(count: Int =8): Result<List<Product>>{

        return withContext(Dispatchers.IO){
            try {
                val querySnapshot = db.collection("products").get().await()

                val allProduct=querySnapshot.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }

                val randomProducts= allProduct.shuffled().take(count)

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

    suspend fun getProductByIds(productIds: List<String>): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = db.collection("products").whereIn("id",productIds)
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

    // getProductSoldCount
    suspend fun getProductSoldCount(productId:String) : Result<Int> = withContext(Dispatchers.IO){
        val listOrderStatus = mutableListOf(OrderStatus.DELIVERED.name,OrderStatus.RATED.name)
        try {
            val querySnapshot = db.collection("orders").whereIn("status",listOrderStatus)
                .get().await()

            var soldCount = 0

            for(document  in querySnapshot.documents){
                val order = document.toObject(Order::class.java)
                order?.items?.find {
                    it.productId == productId
                }?.let {
                    item ->
                    soldCount +=item.quantity
                }
            }

            Result.success(soldCount)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}