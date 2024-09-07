package com.nhathuy.gas24h_7app.ui.order

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.OrderItemAdapter
import com.nhathuy.gas24h_7app.data.model.Cart
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityOrderBinding
import com.nhathuy.gas24h_7app.ui.choose_voucher.ChooseVoucherActivity
import com.nhathuy.gas24h_7app.util.Constants
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import javax.inject.Inject

class OrderActivity : AppCompatActivity(),OrderContract.View{
    private lateinit var binding:ActivityOrderBinding
    private lateinit var orderItemAdapter: OrderItemAdapter
    private var totalAmount=0.0
    private var voucherDiscount = 0.0
    @Inject
    lateinit var presenter: OrderPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)

        val selectedItems = intent.getSerializableExtra("SELECTED_ITEMS") as? ArrayList<CartItem>
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)
        voucherDiscount = intent.getDoubleExtra("SELECTED_VOUCHER_DISCOUNT",0.0)


        // Get voucher information from cart if available
        val voucherId = intent.getStringExtra("VOUCHER_ID")
        val voucherDiscount = intent.getDoubleExtra("VOUCHER_DISCOUNT", 0.0)
        val voucherDiscountType = intent.getStringExtra("VOUCHER_DISCOUNT_TYPE")


        setupToolbar()
        setupVoucherSection()
        setupOrderSuccess()
        orderItemAdapter= OrderItemAdapter()
        binding.orderRec.apply {
            layoutManager=LinearLayoutManager(this@OrderActivity)
            adapter=orderItemAdapter
        }
//        binding.orderTotalPrice.text=NumberFormatUtils.formatPrice(totalAmount)
//        binding.orderPrice.text=NumberFormatUtils.formatPrice(totalAmount)

        presenter.attachView(this)
        selectedItems?.let {
            presenter.loadOrderItems(it)
        }

        // Apply voucher from cart if it exists
        if (voucherId != null) {
            presenter.setInitialVoucher(voucherId, voucherDiscount, voucherDiscountType)
        }

    }

    private fun setupOrderSuccess() {
        binding.btnBuy.setOnClickListener {
            presenter.placeOrder()
        }
    }

    private fun setupVoucherSection() {
        binding.chooseVoucherText.setOnClickListener {
            navigateToChooseVoucher()
        }
        binding.priceReduceOfVoucher.setOnClickListener {
            navigateToChooseVoucher()
        }
    }

    private fun navigateToChooseVoucher() {
        val intent = Intent(this, ChooseVoucherActivity::class.java)
        intent.putExtra("HAS_SELECTED_PRODUCTS", true)
        intent.putExtra("CURRENT_VOUCHER_ID", presenter.getCurrentVoucherId())
        intent.putExtra("TOTAL_AMOUNT", totalAmount)
        startActivityForResult(intent, Constants.CHOOSE_VOUCHER_REQUEST_CODE)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun showOrderItems(
        items: MutableList<CartItem>,
        products: MutableMap<String, Product>
    ) {
        orderItemAdapter.updateData(items,products)
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun updateVoucherInfo(voucherDiscount: String?) {
        binding.priceReduceOfVoucher.apply {
            text= voucherDiscount
            visibility = if (voucherDiscount != null) View.VISIBLE else View.GONE
        }
        binding.chooseVoucherText.visibility = if (voucherDiscount != null) View.GONE else View.VISIBLE
    }

    override fun updateTotalAmount(totalAmount: Double, discountedAmount: Double) {
        binding.orderTotalPrice.text = NumberFormatUtils.formatPrice(discountedAmount)
        binding.orderPrice.text = NumberFormatUtils.formatPrice(totalAmount)
        binding.orderPriceVoucher.text= "- ${NumberFormatUtils.formatPrice(totalAmount-discountedAmount)}"
        binding.totalPrice.text = NumberFormatUtils.formatPrice(discountedAmount)
    }

    override fun showUserInfo(user: User) {
        binding.orderNameCustomer.text=user.fullName
        binding.orderPhonenumber.text=user.phoneNumber
        binding.orderLocation.text=user.address
    }

    override fun showSuccess() {
        Toast.makeText(this,"Thanh toán thành công",Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CHOOSE_VOUCHER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val voucherId  =data?.getStringExtra("SELECT_VOUCHER_ID")
            val voucherCode = data?.getStringExtra("SELECT_VOUCHER_CODE")
            val voucherDiscount = data?.getDoubleExtra("SELECTED_VOUCHER_DISCOUNT", 0.0) ?: 0.0
            val voucherType = data?.getStringExtra("SELECTED_VOUCHER_TYPE")
//            // Update UI to show selected voucher
            voucherId?.let {
                presenter.applyVoucher(it, voucherDiscount, voucherType)
            } ?: run {
                presenter.removeVoucher()
            }

        }
    }
}