package com.nhathuy.gas24h_7app.admin.product_management.edit_product

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.data.repository.CategoryRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.util.Constants.MAX_IMAGE_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class EditProductPresenter @Inject constructor(
    private val context: Context,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : EditProductContract.Presenter {

    private var view: EditProductContract.View? = null
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var currentProduct: Product
    private val imageUris = mutableListOf<Uri>()
    private var coverImageUri: Uri? = null
    private var categories: List<ProductCategory> = listOf()

    override fun attachView(view: EditProductContract.View) {
        this.view = view
        loadCategories()
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadProduct(productId: String) {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val result = productRepository.getProductById(productId)
                if (result.isSuccess) {
                    currentProduct = result.getOrThrow()
                    withContext(Dispatchers.Main) {
                        view?.populateProductData(currentProduct)
                        val category = categoryRepository.getCategoryById(currentProduct.categoryId)
                        category?.let {
                            view?.setProductCategory(it.categoryName)
                        }
                    }
                } else {
                    view?.showError("Failed to load product: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                view?.showError("Error loading product: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    override fun updateProduct() {
        coroutineScope.launch {
            view?.showLoading()
            try {
                if (!validateInputs()) {
                    return@launch
                }

                val updatedImageUrls = uploadImages()
                val updatedCoverImageUrl = uploadCoverImage() ?: currentProduct.coverImageUrl

                val updatedProduct = currentProduct.copy(
                    name = view?.getProductName() ?: "",
                    categoryId = getSelectedCategoryId(view?.getSelectedCategoryPosition() ?: -1),
                    description = view?.getProductDescription() ?: "",
                    price = view?.getProductPrice()?.toDoubleOrNull() ?: 0.0,
                    stockCount = view?.getProductStockCount()?.toIntOrNull() ?: 0,
                    offerPercentage = view?.getProductOfferPercentage()?.toDoubleOrNull() ?: 0.0,
                    coverImageUrl = updatedCoverImageUrl,
                    detailImageUrls = updatedImageUrls
                )

                productRepository.updateProduct(updatedProduct).onSuccess {
                    view?.showSuccess("Product updated successfully")
                }.onFailure {
                    view?.showError("Failed to update product: ${it.message}")
                }
            } catch (e: Exception) {
                view?.showError("Error updating product: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    override fun onImageAdded(uri: Uri) {
        if (imageUris.size < MAX_IMAGE_COUNT) {
            imageUris.add(uri)
            view?.addImageToAdapter(uri.toString())
            view?.updateImageCount(imageUris.size, MAX_IMAGE_COUNT)
            view?.enableImageAddButton(imageUris.size < MAX_IMAGE_COUNT)
        } else {
            view?.showError("Maximum number of images reached")
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

    override fun onCoverImageAdded(uri: Uri) {
        coverImageUri = uri
        view?.updateCoverImage(uri.toString())
        view?.enableCoverImageAddButton(false)
        view?.updateCoverImageCount(1, 1)
    }

    override fun onCoverImageRemoved() {
        coroutineScope.launch {
            coverImageUri?.let { uri ->
                try {
                    if (currentProduct.coverImageUrl.isNotEmpty()) {
                        val ref = storage.getReferenceFromUrl(currentProduct.coverImageUrl)
                        ref.delete().await()
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    view?.showError("Failed to delete cover image: ${e.message}")
                }
            }
            coverImageUri = null
            view?.clearCoverImage()
            view?.enableCoverImageAddButton(true)
            view?.updateCoverImageCount(0, 1)
        }
    }

    override fun loadCategories() {
        coroutineScope.launch {
            try {
                val result = db.collection("categories").get().await()
                categories = result.mapNotNull { document ->
                    document.toObject(ProductCategory::class.java)
                }
                withContext(Dispatchers.Main) {
                    view?.updateCategoryList(categories.map { it.categoryName })
                }
            } catch (e: Exception) {
                view?.showError("Failed to load categories: ${e.message}")
            }
        }
    }

    override fun getSelectedCategoryId(position: Int): String {
        return if (position in categories.indices) {
            categories[position].id
        } else {
            ""
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val name = view?.getProductName()?.trim() ?: ""
        val categoryId = getSelectedCategoryId(view?.getSelectedCategoryPosition() ?: -1)
        val description = view?.getProductDescription()?.trim() ?: ""
        val priceString = view?.getProductPrice()?.trim() ?: ""
        val offerPercentageString = view?.getProductOfferPercentage()?.trim() ?: ""
        val stockCountString = view?.getProductStockCount()?.trim() ?: ""

        if (name.isBlank()) {
            view?.showNameError("Product name is required")
            isValid = false
        } else {
            view?.showNameError(null)
        }

        if (categoryId == null) {
            view?.showCategoryError("Please select a category")
            isValid = false
        } else {
            view?.showCategoryError(null)
        }

        if (description.isBlank()) {
            view?.showDescriptionError("Description is required")
            isValid = false
        } else {
            view?.showDescriptionError(null)
        }

        val price = priceString.toDoubleOrNull()
        if (price == null || price <= 0) {
            view?.showPriceError("Please enter a valid price")
            isValid = false
        } else {
            view?.showPriceError(null)
        }

        val stock = stockCountString.toIntOrNull()
        if (stock == null || stock < 0) {
            view?.showProductStockCountError("Please enter a valid stock count")
            isValid = false
        } else {
            view?.showProductStockCountError(null)
        }

        val offerPercentage = offerPercentageString.toDoubleOrNull() ?: 0.0
        if (offerPercentage < 0 || offerPercentage > 100) {
            view?.showOfferPercentageError("Offer percentage must be between 0 and 100")
            isValid = false
        } else {
            view?.showOfferPercentageError(null)
        }

        if (imageUris.isEmpty() && currentProduct.detailImageUrls.isEmpty()) {
            view?.showImageError("Please add at least one image")
            isValid = false
        }

        if (coverImageUri == null && currentProduct.coverImageUrl.isEmpty()) {
            view?.showImageError("Please add a cover image")
            isValid = false
        }

        return isValid
    }

    private suspend fun uploadImages(): List<String> = withContext(Dispatchers.IO) {
        val uploadedUrls = mutableListOf<String>()
        uploadedUrls.addAll(currentProduct.detailImageUrls)

        for (uri in imageUris) {
            try {
                val url = uploadImage(uri)
                if (url.isNotEmpty()) {
                    uploadedUrls.add(url)
                }
            } catch (e: Exception) {
                continue
            }
        }
        uploadedUrls
    }

    private suspend fun uploadImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val filename = getFileName(uri)
        val uniqueFileName = generateUniqueFileName(filename)
        val ref = storage.reference.child("product_images/$uniqueFileName")

        try {
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun uploadCoverImage(): String? = withContext(Dispatchers.IO) {
        coverImageUri?.let { uri ->
            try {
                val fileName = getFileName(uri)
                val uniqueFileName = generateUniqueFileName(fileName)
                val ref = storage.reference.child("product_cover_image/$uniqueFileName")

                ref.putFile(uri).await()
                ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                throw e
            }
        }
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
}