package com.nhathuy.gas24h_7app.fragment.categories

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class ProductListCategoryPresenter @Inject constructor(private val db:FirebaseFirestore):ProductListCategoryContract.Presenter{

    private var view:ProductListCategoryContract.View?=null

    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: ProductListCategoryContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun fetchProduct(categoryId: String) {
        view?.showLoading()
        coroutineScope.launch {
            try {
                val products=getProducts(categoryId)
                view?.showProducts(products)
            }
            catch (e:Exception){
                view?.showError(e.message?:"Unknown Error")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    private suspend fun getProducts(categoryId: String): List<Product> = withContext(Dispatchers.IO){
        val querySnapshot= db.collection("products")
            .whereEqualTo("categoryId",categoryId)
            .get().await()
        return@withContext querySnapshot.documents.mapNotNull { it.toObject(Product::class.java) }
    }

}