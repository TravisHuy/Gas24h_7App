package com.nhathuy.gas24h_7app.admin.qrcode

import android.graphics.Bitmap
import com.nhathuy.gas24h_7app.admin.product_management.all_product.AllProductContract
import com.nhathuy.gas24h_7app.data.model.Product

interface QrCodeContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message:String)
        fun showProducts(products:List<Product>)
        fun showQrCodeDialog(bitmap: Bitmap, productName: String)
        fun navigateAllQrCode()
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
        fun loadProducts()
        fun generateQrCode(product: Product)
    }
}