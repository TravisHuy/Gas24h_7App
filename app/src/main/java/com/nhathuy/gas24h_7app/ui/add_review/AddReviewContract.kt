
package com.nhathuy.gas24h_7app.ui.add_review

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Product

interface AddReviewContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showInformationProduct(product:Product)
        fun updateImageCount(count:Int,max:Int)
        fun updateVideoCount(count: Int, max: Int)
        fun onImageAdded(uri: Uri)
        fun onVideoAdded(uri: Uri)
        fun addImageToAdapter(imageUrl: String)
        fun removeImageFromAdapter(position: Int)
        fun enableImageAddButton(enable:Boolean)
        fun enableCoverImageAddButton(enable: Boolean)
        fun clearInputField()
        fun clearImages()
        fun clearVideo()
        fun navigateBack()
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadOrder(orderId:String)
        fun onImageAdded(uri: Uri)
        fun onImageRemoved(position:Int)
        fun onVideoAdded(uri: Uri)
        fun onVideoRemoved()
        fun submitReview(rating:Float, comment:String)
    }
}
