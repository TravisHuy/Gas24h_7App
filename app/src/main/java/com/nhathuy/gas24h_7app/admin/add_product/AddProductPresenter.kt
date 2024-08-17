package com.nhathuy.gas24h_7app.admin.add_product

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private var coverImageUri: Uri? = null
    private val imageUrls = mutableListOf<String>()
    private var coverImageUrl: String = ""
    private var categories: List<ProductCategory> = listOf()
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
      launch {
          view?.showLoading()
          try {
              val productId = db.collection("products").document().id

              //validate input
              val name = view?.getProductName()?.trim() ?: ""
              val categoryId = view?.getSelectedCategoryId()
              val description = view?.getProductDescription()?.trim() ?: ""
              val priceString = view?.getProductPrice()?.trim() ?: ""
              val offerPercentageString = view?.getProductOfferPercentage()?.trim() ?: ""
              val stockCountString = view?.getProductStockCount()?.trim() ?: ""

              if (!validateInputs(name, categoryId, description, priceString, offerPercentageString,stockCountString)) {
                  return@launch
              }

              val price = priceString.toDouble()
              val stockCount = stockCountString.toInt()
              val offerPercentage = offerPercentageString.toDoubleOrNull() ?: 0.0

              val uploadedImageUrls = uploadImages()
              val uploadedCoverImageUrl = uploadCoverImage()

              if (uploadedImageUrls.isEmpty()) {
                  view?.showError("Failed to upload any images. Please try again.")
                  return@launch
              }
              if (uploadedCoverImageUrl.isEmpty()) {
                  view?.showError("Failed to upload cover image. Please try again.")
                  return@launch
              }

              // Create product object

              val product = Product(
                  id = productId,
                  name = name,
                  categoryId = categoryId!!, // Assume category exists
                  description = description,
                  price = price!!,
                  stockCount = stockCount,
                  offerPercentage = offerPercentage,
                  detailImageUrls = uploadedImageUrls,
                  coverImageUrl = uploadedCoverImageUrl
              )

              // Add product to Firestore
              db.collection("products").document(productId).set(product.toMap()).await()

              withContext(Dispatchers.Main) {
                  view?.showSuccess("Product added successfully")
                  view?.clearInputFields()
                  clearImages()
              }
          } catch (e: Exception) {
              withContext(Dispatchers.Main) {
                  view?.showError("Failed to add product: ${e.message}")
              }
          }
          finally {
              view?.hideLoading()
          }
      }

    }


    //clear images after submit
    private fun clearImages() {
        imageUris.clear()
        view?.updateImageCount(0, MAX_IMAGE_COUNT)
        view?.clearImages()
        view?.enableImageAddButton(true)
        view?.enableCoverImageAddButton(true)
        view?.clearCoverImage()
        view?.updateCoverImageCount(0, 1)
    }

    private fun validateInputs(
        name: String,
        categoryId: String?,
        description: String,
        priceString: String,
        offerPercentageString: String,
        stockCountString:String
    ): Boolean {
        var isValid = true

        if (name.isBlank()) {
            view?.showNameError("Product name is required")
            isValid = false
        }
        if (categoryId == null) {
            view?.showCategoryError("Please select a category")
            isValid = false
        }
        else {
            view?.showCategoryError(null)  // Clear any previous error
        }
        if (description.isBlank()) {
            view?.showDescriptionError("Description is required")
            isValid = false
        }
        val price = priceString.toDoubleOrNull()
        if (price == null || price <= 0) {
            view?.showPriceError("Please enter a valid price")
            isValid = false
        }
        val stock = stockCountString.toIntOrNull()
        if (stock == null || stock <= 0) {
            view?.showProductStockCountError("Please enter a valid stock count")
            isValid = false
        }

        val offerPercentage = offerPercentageString.toDoubleOrNull() ?: 0.0
        if (offerPercentage < 0 || offerPercentage > 100) {
            view?.showOfferPercentageError("Offer percentage must be between 0 and 100")
            isValid = false
        }
        if (imageUris.isEmpty()) {
            view?.showImageError("Please add at least one image")
            isValid = false
        }
        if (coverImageUri==null) {
            view?.showImageError("Please add a cover image")
            isValid = false
        }
        return isValid
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

    override fun onCoverImageAdded(uri: Uri) {
        coverImageUri = uri
        view?.updateCoverImage(uri.toString())
        view?.enableCoverImageAddButton(false)
        view?.updateCoverImageCount(1,1)
    }

    override fun onCoverImageRemoved() {
        launch {
            coverImageUri?.let {
                uri ->
                try {
                    if(coverImageUrl.isNotEmpty()){
                        val ref= storage.getReferenceFromUrl(coverImageUrl)
                        ref.delete().await()
                    }
                    else{

                    }
                }
                catch (e:Exception){
                    view?.showError("Failed to delete cover image: ${e.message}")
                }
            }
            coverImageUri=null
            coverImageUrl=""
            view?.clearCoverImage()
            view?.enableCoverImageAddButton(true)
            view?.updateCoverImageCount(0,1)
        }
    }

    private suspend fun uploadImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val filename = getFileName(uri)
        val uniqueFileName = generateUniqueFileName(filename)
        val ref = storage.reference.child("product_images/$uniqueFileName")

        try {
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            downloadUrl  // Return the download URL
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                view?.showError("Failed to upload image: ${e.message}")
            }
            ""  // Return an empty string in case of failure
        }
    }

    private suspend fun uploadCoverImage(): String = withContext(Dispatchers.IO) {
        coverImageUri?.let { uri ->
            try {

                val fileName = getFileName(uri)
                val uniqueFileName = generateUniqueFileName(fileName)

                val ref = storage.reference.child("product_cover_image/$uniqueFileName")

                ref.putFile(uri).await()

                val downloadUrl = ref.downloadUrl.await().toString()

                downloadUrl
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Failed to upload cover image: ${e.message}")
                }
                ""
            }
        } ?: ""
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

                //get the current highest category Id
                val highestIdDoc = db.collection("categories")
                    .orderBy("id",Query.Direction.DESCENDING)
                    .limit(1)
                    .get().await()
                val newId= if(!highestIdDoc.isEmpty){
                    val highestId= highestIdDoc.documents[0].getString("id")?.toIntOrNull()?:0
                    (highestId+1).toString()
                }else{
                    "1"
                }

                val category = ProductCategory(
                    id = newId,
                    categoryName = categoryName
                )

                db.collection("categories").document(newId)
                    .set(category.toMap()).await()
                withContext(Dispatchers.Main) {
                    view?.showSuccess("Category added successfully")
                    loadCategories()
                }
            } catch (e: Exception) {
                view?.showError("Failed to add category: ${e.message}")
            }
        }
    }

    override fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("categories").get().await()
                categories = result.mapNotNull { document ->
                    document.toObject(ProductCategory::class.java)
                }
                withContext(Dispatchers.Main) {
                    view?.updateCategoryList(categories.map {
                        it.categoryName
                    })
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Failed to load categories: ${e.message}")
                    Log.d("AddProductPresenter","Failed to load categories: ${e.message}")
                }
            }
        }
    }
    private suspend fun uploadImages(): List<String> = withContext(Dispatchers.IO) {
        val uploadedUrls = mutableListOf<String>()
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
    override fun getSelectedCategoryId(position: Int): String? {
        return if (position in categories.indices) {
            categories[position].id
        } else {
            null
        }
    }
}