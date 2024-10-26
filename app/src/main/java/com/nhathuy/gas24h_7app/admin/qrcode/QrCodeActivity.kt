package com.nhathuy.gas24h_7app.admin.qrcode

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.QrCodeAdapter
import com.nhathuy.gas24h_7app.admin.qrcode.all.AllQrCodeActivity
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityQrCodeBinding
import com.nhathuy.gas24h_7app.databinding.DialogQrCodeBinding
import javax.inject.Inject

class QrCodeActivity : AppCompatActivity(), QrCodeContract.View {
    private lateinit var binding: ActivityQrCodeBinding
    private lateinit var adapter: QrCodeAdapter
    private var qrCodeDialog: Dialog? = null

    @Inject
    lateinit var presenter: QrCodePresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)
        setupRecycler()
        setupSwipeRefresh()
        setupListeners()
        presenter.loadProducts()
    }
    private fun setupRecycler() {
        binding.recyclerViewAllQr.layoutManager = GridLayoutManager(this, 2)
        adapter = QrCodeAdapter(onQrButtonClick = { product ->
            presenter.generateQrCode(product)
        })
        binding.recyclerViewAllQr.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayoutAllQr.setOnRefreshListener {
            presenter.loadProducts()
        }
    }
    private fun setupListeners() {
        binding.btnAllQr.setOnClickListener {
            navigateAllQrCode()
        }
    }

    override fun showLoading() {
        binding.swipeRefreshLayoutAllQr.isRefreshing = true
    }

    override fun hideLoading() {
        binding.swipeRefreshLayoutAllQr.isRefreshing = false
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProducts(products: List<Product>) {
        adapter.updateData(products)
    }

    override fun showQrCodeDialog(bitmap: Bitmap, productName: String) {
        qrCodeDialog?.dismiss()

        val dialogBinding = DialogQrCodeBinding.inflate(layoutInflater)

        qrCodeDialog = Dialog(this).apply {
            setContentView(dialogBinding.root)

            dialogBinding.ivQrCode.setImageBitmap(bitmap)
            dialogBinding.tvProductName.text = productName

            dialogBinding.btnClose.setOnClickListener {
                dismiss()
            }

            show()
        }
    }

    override fun navigateAllQrCode() {
        startActivity(Intent(this,AllQrCodeActivity::class.java))
        finish()
    }
}