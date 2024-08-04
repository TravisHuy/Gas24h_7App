package com.nhathuy.gas24h_7app.admin.add_product

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.util.Constants.MAX_IMAGE_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddProductPresenter @Inject constructor(private val context:Context,
                          private val db:FirebaseFirestore,
                          private val storage: FirebaseStorage
):AddProductContract.Presenter,CoroutineScope {

    private var view: AddProductContract.View? = null
    private val imageUris = mutableListOf<Uri>()
    private val imageUrls = mutableListOf<String>()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun attachView(view: AddProductContract.View) {
        this.view = view
        loadCategories()
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun addProduct() {
        val productId = db.collection("products").document().id

        val name = view?.getProductName() ?: ""
        val category = view?.getProductCategory() ?: ""
        val description = view?.getProductDescription() ?: ""
        val price = view?.getProductPrice()?.toDoubleOrNull() ?: 0.0
        val offerPercentage = view?.getProductOfferPercentage()?.toDoubleOrNull() ?: 0.0


    }

    override fun onImageAdded(uri: Uri) {
        if (imageUris.size < MAX_IMAGE_COUNT) {
            imageUris.add(uri)
            view?.addImageToAdapter(uri.toString())
            view?.updateImageCount(imageUris.size, MAX_IMAGE_COUNT)
            view?.enableImageAddButton(imageUris.size < MAX_IMAGE_COUNT)
        } else {
            view?.showError("Maximum number of image reached")
        }
    }

    override fun onImageRemoved(position: Int) {
        if (position in imageUris.indices) {
            imageUris.removeAt(position)
            view?.removeImageFromAdapter(position)
            view?.updateImageCount(imageUris.size, MAX_IMAGE_COUNT)
            view?.enableImageAddButton(true)
        }
    }

    private suspend fun uploadImage(uri: Uri) = withContext(Dispatchers.IO) {
        val filename = getFileName(uri)
        val uniqueFileName = generateUniqueFileName(filename)
        val ref = storage.reference.child("product_images/$uniqueFileName")

        try {
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()

            withContext(Dispatchers.Main) {
                imageUrls.add(downloadUrl)
                updateImageCountView()
                view?.addImageToAdapter(downloadUrl)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                view?.showError("Failed to upload image: ${e.message}")
            }
        }
    }

    private fun updateImageCountView() {
        view?.updateImageCount(imageUrls.size, MAX_IMAGE_COUNT)
        view?.enableImageAddButton(imageUrls.size < MAX_IMAGE_COUNT)
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        result = it.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }

        return result ?: "unknown.jpg"
    }

    private fun generateUniqueFileName(fileName: String): String {
        val nameWithoutExtension = fileName.substringBeforeLast(".")
        val extension = fileName.substringAfterLast(".", "")
        val uniqueId = UUID.randomUUID().toString().substring(0, 8)
        return if (extension.isNotEmpty()) {
            "${nameWithoutExtension}_${uniqueId}.$extension"
        } else {
            "${nameWithoutExtension}_${uniqueId}"
        }
    }


    private suspend fun deleteImageFromStorage(imageUrl: String) {
        val ref = storage.getReferenceFromUrl(imageUrl)
        try {
            ref.delete().await()
        } catch (e: Exception) {
            view?.showError("Failed to delete image: ${e.message}")
        }
    }

    override fun addCategory(categoryName: String)  {
        if (categoryName.isBlank()) {
            view?.showError("Category name cannot be empty")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val category = ProductCategory(
                    id = db.collection("categories").document().id,
                    categoryName = categoryName
                )

                db.collection("categories").document(category.id)
                    .set(ProductCategory.toMap(category)).await()
                withContext(Dispatchers.Main) {
                    view?.showSuccess("Category added successfully")
                    loadCategories()
                }
            } catch (e: Exception) {
                view?.showError("Failed to add category: ${e.message}")
            }
        }
    }

    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("categories").get().await()
                val categories = result.mapNotNull { document ->
                    document.data.let {
                        ProductCategory.fromMap(it)
                    }
                }
                withContext(Dispatchers.Main) {
                    view?.updateCategoryList(categories)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Failed to load categories: ${e.message}")
                }
            }
        }
    }
}