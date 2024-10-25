package com.nhathuy.gas24h_7app.admin.qrcode

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.nhathuy.gas24h_7app.admin.product_management.all_product.AllProductContract
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class QrCodePresenter @Inject constructor(private val productRepository: ProductRepository):QrCodeContract.Presenter {
    private var view:QrCodeContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun attachView(view: QrCodeContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadProducts() {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val result = productRepository.getAllProducts()
                result.fold(
                    onSuccess = { products ->
                        view?.showProducts(products)
                    },
                    onFailure = {
                            e ->
                        view?.showMessage("Failed load products: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Failed load products: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun generateQrCode(product: Product) {
        try {
            val qrContent = createQrContent(product)
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix = multiFormatWriter.encode(
                qrContent,
                BarcodeFormat.QR_CODE,
                500, // width
                500  // height
            )

            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

            view?.showQrCodeDialog(bitmap, product.name)
        }
        catch (e:Exception){
            view?.showMessage("Failed to generate QR code: ${e.message}")
        }
    }

    private fun createQrContent(product: Product): String {
        return "PRODUCT:${product.id}:${product.name}"
    }

}