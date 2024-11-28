package com.nhathuy.gas24h_7app.test_add_review

import android.net.Uri
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.ui.add_review_test.AddReviewTestContract

class MockAddReviewTestView:AddReviewTestContract.View {
    override fun showLoading() {}
    override fun hideLoading() {}
    override fun showMessage(message: String) {}
    override fun showInformationProduct(product: Product) {}
    override fun updateImageCount(count: Int, max: Int) {}
    override fun updateVideoCount(count: Int, max: Int) {}
    override fun onImageAdded(uri: Uri) {}
    override fun onVideoAdded(uri: Uri) {}
    override fun addImageToAdapter(imageUrl: String) {}
    override fun removeImageFromAdapter(position: Int) {}
    override fun enableImageAddButton(enable: Boolean) {}
    override fun enableCoverImageAddButton(enable: Boolean) {}
    override fun clearInputField() {}
    override fun clearImages() {}
    override fun clearVideo() {}
    override fun navigateBack() {}
    override fun showProducts(products: List<Product>) {}
    override fun loadSavedReviewData(
        rating: Float,
        comment: String,
        images: List<Uri>,
        video: Uri?
    ) {}
}