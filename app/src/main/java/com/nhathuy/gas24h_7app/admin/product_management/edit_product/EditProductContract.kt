package com.nhathuy.gas24h_7app.admin.product_management.edit_product

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Product

interface EditProductContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(message: String)
        fun updateImageCount(count: Int, max: Int)
        fun updateCoverImage(imageUrl: String)
        fun updateCoverImageCount(count: Int, max: Int)
        fun addImageToAdapter(imageUrl: String)
        fun removeImageFromAdapter(position: Int)
        fun enableImageAddButton(enable: Boolean)
        fun enableCoverImageAddButton(enable: Boolean)
        fun setProductName(name: String)
        fun setProductPrice(price: String)
        fun setProductOfferPercentage(percentage: String)
        fun setProductCategory(category: String)
        fun setProductDescription(description: String)
        fun setProductStockCount(count: String)
        fun getProductName(): String
        fun getSelectedCategoryPosition(): Int
        fun getProductDescription(): String
        fun getProductPrice(): String
        fun getProductStockCount(): String
        fun getProductOfferPercentage(): String
        fun updateCategoryList(categories: List<String>)
        fun showNameError(error: String?)
        fun showCategoryError(error: String?)
        fun showDescriptionError(error: String?)
        fun showPriceError(error: String?)
        fun showOfferPercentageError(error: String?)
        fun showProductStockCountError(error: String?)
        fun showImageError(error: String?)
        fun clearImages()
        fun clearCoverImage()
        fun populateProductData(product: Product)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadProduct(productId: String)
        fun updateProduct()
        fun onImageAdded(uri: Uri)
        fun onImageRemoved(position: Int)
        fun onCoverImageAdded(uri: Uri)
        fun onCoverImageRemoved()
        fun loadCategories()
        fun getSelectedCategoryId(position: Int): String
    }
}