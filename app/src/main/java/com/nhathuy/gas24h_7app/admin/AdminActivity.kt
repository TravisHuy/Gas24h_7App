package com.nhathuy.gas24h_7app.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.admin.chat.ChatActivity
import com.nhathuy.gas24h_7app.admin.notification.add_notification.AddNotificationActivity
import com.nhathuy.gas24h_7app.admin.order.shipping.ShippingActivity
import com.nhathuy.gas24h_7app.admin.print_invoice.PrintInvoiceActivity
import com.nhathuy.gas24h_7app.admin.product_management.all_product.AllProductActivity
import com.nhathuy.gas24h_7app.admin.revenue_statistics.RevenueStatisticsActivity
import com.nhathuy.gas24h_7app.admin.voucher.all_product.VoucherAllProductActivity
import com.nhathuy.gas24h_7app.admin.voucher.detail_product.VoucherDetailProductActivity
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.databinding.ActivityAdminBinding
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject

class AdminActivity : AppCompatActivity(),AdminContract.View {

    private lateinit var binding:ActivityAdminBinding

    @Inject
    lateinit var presenter: AdminPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)
        presenter.loadOrderShipping()
        presenter.loadOrderCancel()
        setupNavigate()
    }

    private fun setupNavigate() {

        //backhome
        binding.backHome.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        // transfer shipping
        binding.linearShipping.setOnClickListener {
            startActivity(Intent(this,ShippingActivity::class.java))
        }
        //all product
        binding.linearAllProduct.setOnClickListener {
            startActivity(Intent(this,AllProductActivity::class.java))
        }
        // add voucher
        binding.linearAddVoucher.setOnClickListener {
            showDialogAddVoucher()
        }
        //
        binding.linearRevenueStatistics.setOnClickListener {
            startActivity(Intent(this,RevenueStatisticsActivity::class.java))
        }

        binding.linearNotification.setOnClickListener {
            startActivity(Intent(this, AddNotificationActivity::class.java))
        }
        binding.linearPrintOrder.setOnClickListener {
            startActivity(Intent(this,PrintInvoiceActivity::class.java))
        }
        binding.floatingActionButtonChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

    private fun showDialogAddVoucher() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_voucher, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btn_add_voucher_product).setOnClickListener {
            startActivity(Intent(this,VoucherDetailProductActivity::class.java))
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_add_voucher_all).setOnClickListener {
            // Xử lý thêm voucher cho tất cả sản phẩm
            startActivity(Intent(this,VoucherAllProductActivity::class.java))
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        Log.d("Admin","${message}")
    }

    override fun showCountOrderShipping(count: Int) {
        binding.tvCountOrderShipping.text = String.format(count.toString())
    }

    override fun showCountOrderCancel(count: Int) {
        binding.tvCountOrderCancel.text = String.format(count.toString())
    }

    override fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}