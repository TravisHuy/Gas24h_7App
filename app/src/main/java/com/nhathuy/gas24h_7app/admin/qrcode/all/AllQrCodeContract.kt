package com.nhathuy.gas24h_7app.admin.qrcode.all

import android.graphics.Bitmap

interface AllQrCodeContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showMessage(message: String)
        fun showQrCodes(qrData: List<Triple<String, String, Bitmap>>)
        fun updateSelectedCount(count: Int)
        fun showSelectionMode(show: Boolean)
    }
    interface Presenter{
        fun attachView(view: View)
        fun detachView()
        fun loadAllQrCodes()
        fun saveQrCodeToGallery(position: Int)
        fun shareQrCode(position: Int)
        fun toggleSelection(position: Int)
        fun clearSelections()
        fun getSelectedCount(): Int
        fun printSelectedQrCodes()
        fun printAllQrCodes()
    }
}