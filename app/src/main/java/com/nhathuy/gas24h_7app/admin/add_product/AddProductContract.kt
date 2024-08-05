package com.nhathuy.gas24h_7app.admin.add_product

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.ui.register.RegisterContract

interface AddProductContract {
    interface View{
        fun showError(message:String)
        fun showSuccess(message: String)
        fun updateImageCount(count:Int,max:Int)
        fun addImageToAdapter(imageUrl: String)
        fun removeImageFromAdapter(position: Int)
        fun enableImageAddButton(enable:Boolean)
        fun clearInputFields()
        fun getProductName():String
        fun getProductPrice():String
        fun getProductOfferPercentage():String
        fun getProductCategory():String
        fun getProductDescription():String
        fun updateCategoryList(categories: List<String>)
        fun getSelectedCategoryId():String?
        fun showNameError(error:String?)
        fun showCategoryError(error:String?)
        fun showDescriptionError(error:String?)
        fun showPriceError(error:String?)
        fun showOfferPercentageError(error:String?)
        fun showImageError(error:String?)

        fun clearImages()
    }
    interface Presenter{
        fun attachView(view: AddProductContract.View)
        fun detachView()
        fun addProduct()
        fun onImageAdded(uri:Uri)
        fun onImageRemoved(position:Int)
        fun addCategory(categoryName:String)
        fun loadCategories()
        fun getSelectedCategoryId(position: Int):String?
    }
}