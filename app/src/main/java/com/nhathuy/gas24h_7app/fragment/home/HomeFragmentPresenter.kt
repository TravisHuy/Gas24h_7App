package com.nhathuy.gas24h_7app.fragment.home

import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeFragmentPresenter @Inject constructor(private val db:FirebaseFirestore,private val storage:FirebaseStorage):HomeFragmentContract.Presenter {
    private var view:HomeFragmentContract.View?=null

    private val job= SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun attachView(view: HomeFragmentContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
        job.cancel()
    }

    override fun fetchCategories() {
        view?.showLoading()
        coroutineScope.launch {
           try {
               val categories = withContext(Dispatchers.IO) {
                   db.collection("categories").get().await().map { doc ->
                       ProductCategory(
                           id = doc.getString("id") ?: "",
                           categoryName = doc.getString("categoryName") ?: ""
                       )
                   }.sorted()
               }
               view?.showCategories(categories)
           }
           catch (e:Exception){
              view?.showError(e.message ?: "Error fetching categories")
           }
            finally {
                view?.hideLoading()
            }
        }

    }
    override fun fetchBanners() {
        view?.showLoading()
        coroutineScope.launch {
            try {
                val bannerUrls = withContext(Dispatchers.IO){
                    storage.reference.child("banners")
                        .listAll().await()
                        .items
                        .map {
                            item ->
                            item.downloadUrl.await().toString()
                        }
                }
                view?.showBanners(bannerUrls)
            }
            catch (e:Exception){
                view?.showError(e.message ?: "Error fetching banners")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

}