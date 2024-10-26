package com.nhathuy.gas24h_7app.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object QrCodeUtils {
    fun saveQrCodeToGallery(context: Context, bitmap: Bitmap, productName: String): Uri? {
        val filename = "QR_${productName}_${System.currentTimeMillis()}.jpg"
        var fos:OutputStream? = null
        var imageUri : Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                context.contentResolver.also { resolver ->
                    imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            }
            else{
                // For below Android 10
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                imageUri = Uri.fromFile(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(context, "QR Code đã được lưu vào thư viện ảnh", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return imageUri
    }
    fun shareQrCode(context: Context, bitmap: Bitmap, productName: String) {
        try {
            val imageUri = saveQrCodeToGallery(context, bitmap, productName)
            imageUri?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, it)
                    putExtra(Intent.EXTRA_SUBJECT, "QR Code cho sản phẩm: $productName")
                    putExtra(Intent.EXTRA_TEXT, "QR Code cho sản phẩm: $productName")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ QR Code"))
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi khi chia sẻ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun printQrCode(context: Context, qrCodeBitmaps: List<Bitmap>, productNames: List<String>) {
        // Tạo PDF để in
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points
        val qrCodeSize = 200
        val spacing = 20
        val qrCodesPerRow = 2
        val qrCodesPerPage = 6

        var currentQrIndex = 0
        while (currentQrIndex < qrCodeBitmaps.size) {
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentQrIndex / qrCodesPerPage + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            var currentY = spacing
            var row = 0
            while (row < 3 && currentQrIndex < qrCodeBitmaps.size) {
                var col = 0
                while (col < qrCodesPerRow && currentQrIndex < qrCodeBitmaps.size) {
                    val x = spacing + col * (qrCodeSize + spacing)
                    val y = currentY

                    // Draw QR code
                    canvas.drawBitmap(
                        qrCodeBitmaps[currentQrIndex],
                        null,
                        android.graphics.Rect(x, y, x + qrCodeSize, y + qrCodeSize),
                        null
                    )

                    // Draw product name
                    val paint = android.graphics.Paint().apply {
                        textSize = 12f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.drawText(
                        productNames[currentQrIndex],
                        x + qrCodeSize / 2f,
                        y + qrCodeSize + 15f,
                        paint
                    )

                    currentQrIndex++
                    col++
                }
                currentY += qrCodeSize + spacing + 20 // Extra space for text
                row++
            }
            pdfDocument.finishPage(page)
        }

        // Save PDF file
        try {
            val filename = "QR_Codes_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(null), filename)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            // Open PDF with external app
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Mở file PDF với"))
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi khi tạo PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}