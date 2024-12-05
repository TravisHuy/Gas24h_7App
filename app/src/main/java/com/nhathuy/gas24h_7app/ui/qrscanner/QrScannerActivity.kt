package com.nhathuy.gas24h_7app.ui.qrscanner

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.admin.print_invoice.print_invoice_detail.PrintInVoiceDetailActivity
import com.nhathuy.gas24h_7app.databinding.ActivityQrScannerBinding
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import javax.inject.Inject

class QrScannerActivity : AppCompatActivity(),QrScannerContract.View {
    private lateinit var binding: ActivityQrScannerBinding
    private var scanningAnimator: ValueAnimator? = null
    @Inject
    lateinit var presenter: QrScannerPresenter

    private val CAMERA_PERMISSION_REQUEST=100
    private var lastScanResult: String? = null
    private val scanCooldown = 2000L
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupUI()
        initializeScanner()
        checkCameraPermission()
        startScanningAnimation()

    }
    private fun setupUI() {
        binding.root.findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        binding.root.findViewById<View>(R.id.overlay_view)?.let {
            overlay ->
            val scannerFrame = binding.root.findViewById<View>(R.id.scanner_frame)
            val overlayDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#99000000"))
                val frameLocation = IntArray(2)
                scannerFrame.getLocationInWindow(frameLocation)

                setShape(GradientDrawable.RECTANGLE)
                val cornerRadius = resources.getDimensionPixelSize(R.dimen.scanner_corner_radius)
                setStroke(scannerFrame.width, Color.TRANSPARENT)
            }
            overlay.background = overlayDrawable
        }
    }
    private fun initializeScanner() {
        val barcodeView = binding.barcodeScanner.barcodeView

        val formats = listOf(BarcodeFormat.QR_CODE)
        barcodeView.decoderFactory = DefaultDecoderFactory(formats)

        binding.barcodeScanner.decodeContinuous(object:BarcodeCallback{
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    val scannedText = it.text
                    if (scannedText != lastScanResult) {
                        lastScanResult = scannedText
                        handleScanResult(scannedText)

                        handler.postDelayed({
                            lastScanResult = null
                        }, scanCooldown)
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>?) {}
        })
    }
    private fun handleScanResult(result: String) {
        binding.barcodeScanner.pause()

        try{
            when{
                result.startsWith("PRODUCT:") ->{
                    val parts = result.split(":")
                    if(parts.size>=2){
                        val productId = parts[1].trim()
                        if(productId.isNotEmpty()){
                            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                vibrator.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE))
                            }else{
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(100)
                            }
                            navigateToProductDetail(productId)
                        }
                        else{
                            showError("Mã QR không hợp lệ")
                            resumeScanning()
                        }
                    }
                    else{
                        showError("Định dạng mã QR không đúng")
                        resumeScanning()
                    }
                }
                result.startsWith("ORDERS:") ->{
                    val parts = result.split(":")
                    if(parts.size>=2){
                        val orderId = parts[1].trim()
                        if(orderId.isNotEmpty()){
                            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                vibrator.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE))
                            }else{
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(100)
                            }
                            navigateToOrderList(listOf(orderId))
                        }
                        else{
                            showError("Mã QR không hợp lệ")
                            resumeScanning()
                        }
                    }
                    else{
                        showError("Định dạng mã QR không đúng")
                        resumeScanning()
                    }
                }
                else -> {
                    showError("Loại mã QR không đúng")
                    resumeScanning()
                }
            }
        }
        catch (e:Exception){
            showError("Lỗi khi xử lý mã QR")
            resumeScanning()
        }
    }
    private fun resumeScanning() {
        handler.postDelayed({
            binding.barcodeScanner.resume()
        }, 1000)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            binding.barcodeScanner.resume()
        }
    }

    private fun startScanningAnimation() {
        val scanningLine = findViewById<View>(R.id.scanning_line)
        val scannerFrame = findViewById<View>(R.id.scanner_frame)

        scanningAnimator?.cancel()
        scanningAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500 // 1.5 seconds for one complete scan
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator() // Smooth constant speed

            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val translationY = scannerFrame.height * progress
                scanningLine.translationY = translationY
            }

            start()
        }
    }

    override fun navigateToProductDetail(productId: String) {
        val intent = Intent(this, DetailProductActivity::class.java).apply {
            putExtra("PRODUCT_ID", productId)
        }
        startActivity(intent)
        finish()
    }

    override fun navigateToOrderList(orderIds: List<String>) {
        val intent = Intent(this, PrintInVoiceDetailActivity::class.java).apply {
            putStringArrayListExtra("ORDER_IDS", ArrayList(orderIds))
        }
        startActivity(intent)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.barcodeScanner.resume()
        lastScanResult = null
        startScanningAnimation()
    }
    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
        scanningAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanningAnimator?.cancel()
        scanningAnimator = null
        presenter.detachView()
        handler.removeCallbacksAndMessages(null)
    }

}