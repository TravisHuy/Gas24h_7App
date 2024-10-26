package com.nhathuy.gas24h_7app.admin.qrcode.all

import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.util.QrCodeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllQrCodePresenter @Inject constructor(private val productRepository: ProductRepository,
                                             private val context: Context
):AllQrCodeContract.Presenter {

    private var view: AllQrCodeContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private var qrCodeData = listOf<Triple<String, String, Bitmap>>()
    private val selectedPositions = mutableSetOf<Int>()
    override fun attachView(view: AllQrCodeContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadAllQrCodes() {
        coroutineScope.launch {
            view?.showLoading()
            try {
                val result = productRepository.getAllProducts()
                result.fold(
                    onSuccess = { products ->
                        val qrList = products.map { product ->
                            Triple(
                                product.id,
                                product.name,
                                generateQrBitmap(product.id, product.name)
                            )
                        }
                        qrCodeData = qrList
                        view?.showQrCodes(qrList)
                    },
                    onFailure = { e ->
                        view?.showMessage("Không thể tải QR codes: ${e.message}")
                    }
                )
            } catch (e: Exception) {
                view?.showMessage("Lỗi: ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    private fun generateQrBitmap(productId: String, productName: String): Bitmap {
        val qrContent = "PRODUCT:$productId:$productName"
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(
            qrContent,
            BarcodeFormat.QR_CODE,
            500,
            500
        )
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.createBitmap(bitMatrix)
    }

    override fun saveQrCodeToGallery(position: Int) {
        if (position >= 0 && position < qrCodeData.size) {
            val (_, productName, bitmap) = qrCodeData[position]
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    QrCodeUtils.saveQrCodeToGallery(context, bitmap, productName)
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        view?.showMessage("Không thể lưu QR code: ${e.message}")
                    }
                }
            }
        }
    }

    override fun shareQrCode(position: Int) {
        if (position >= 0 && position < qrCodeData.size) {
            val (_, productName, bitmap) = qrCodeData[position]
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    QrCodeUtils.shareQrCode(context, bitmap, productName)
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        view?.showMessage("Không thể chia sẻ QR code: ${e.message}")
                    }
                }
            }
        }
    }

    override fun toggleSelection(position: Int) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
        }

        val selectedCount = selectedPositions.size
        view?.updateSelectedCount(selectedCount)
        view?.showSelectionMode(selectedCount > 0)
    }

    override fun clearSelections() {
        view?.updateSelectedCount(0)
        view?.showSelectionMode(false)
    }

    override fun getSelectedCount(): Int = selectedPositions.size

    override fun printSelectedQrCodes() {
        if (selectedPositions.isEmpty()) {
            view?.showMessage("Chưa chọn QR code nào để in")
            return
        }

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val selectedQrCodes = selectedPositions
                    .filter { it < qrCodeData.size }
                    .map { qrCodeData[it] }

                val bitmaps = selectedQrCodes.map { it.third }
                val productNames = selectedQrCodes.map { it.second }

                QrCodeUtils.printQrCode(context, bitmaps, productNames)
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    view?.showMessage("Không thể in QR codes đã chọn: ${e.message}")
                }
            }
        }
    }

    override fun printAllQrCodes() {
        TODO("Not yet implemented")
    }
}