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
        fun updateCategoryList(categories: List<ProductCategory>)
    }
    interface Presenter{
        fun attachView(view: AddProductContract.View)
        fun detachView()
        fun addProduct()
        fun onImageAdded(uri:Uri)
        fun onImageRemoved(position:Int)
        fun addCategory(categoryName:String)
    }
}