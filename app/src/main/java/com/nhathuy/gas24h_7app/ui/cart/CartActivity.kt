package com.nhathuy.gas24h_7app.ui.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.CartItemAdapter
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityCartBinding
import com.nhathuy.gas24h_7app.ui.choose_voucher.ChooseVoucherActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import com.nhathuy.gas24h_7app.ui.order.OrderActivity
import com.nhathuy.gas24h_7app.util.Constants.CHOOSE_VOUCHER_REQUEST_CODE
import com.nhathuy.gas24h_7app.util.Constants.SELECTED_VOUCHER_KEY
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import javax.inject.Inject
import kotlin.math.roundToInt

class CartActivity : AppCompatActivity(),CartContract.View {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartItemAdapter: CartItemAdapter

    @Inject
    lateinit var presenter: CartPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        cartItemAdapter = CartItemAdapter(
            onQuantityChanged = { productId, newQuantity ->
                presenter.updateCartItemQuantity(productId, newQuantity)
            },
            onQuantityExceeded = {
                    productId, stockCount ->
                presenter.handleManualQuantityExceeded(productId,stockCount)
            },
            onManualQuantityExceeded = { productId,maxCount->
                 presenter.handleManualQuantityExceeded(productId,maxCount)
            },
            onItemChecked = {
                productId,isChecked ->
                presenter.updateItemSelection(productId,isChecked)
            },
            onItemDelete = {
                    productId ->
                presenter.removeProductInCart(productId)
                cartItemAdapter.removeItem(productId)
            }
        )
        binding.recyclerViewCart.apply {
            layoutManager=LinearLayoutManager(context)
            adapter = cartItemAdapter
        }
        binding.cartCheckbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.updateAllItemsSelection(isChecked)
        }
        binding.btnBuy.setOnClickListener {
            presenter.onBtnClicked()
        }
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.btnHome.setOnClickListener {
            navigateHome()
        }
        setupVoucherSection()
        presenter.attachView(this)
        presenter.loadCartItems()
    }

    private fun setupVoucherSection() {
        binding.chooseVoucherText.setOnClickListener {
            navigateToChooseVoucher()
        }
        binding.priceReduceVoucher.setOnClickListener {
            navigateToChooseVoucher()
        }
    }

    private fun navigateToChooseVoucher() {
        val intent = Intent(this,ChooseVoucherActivity::class.java)
        intent.putExtra("HAS_SELECTED_PRODUCTS", presenter.hasSelectedItems())
        intent.putExtra("CURRENT_VOUCHER_ID", presenter.getCurrentVoucherId())
        intent.putExtra("CURRENT_USER_ID", presenter.getCurrentUserId())
        startActivityForResult(intent,CHOOSE_VOUCHER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_VOUCHER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val voucherId  =data?.getStringExtra("SELECT_VOUCHER_ID")

//            // Update UI to show selected voucher
            voucherId?.let {
                presenter.applyVoucher(it)
            } ?: run {
                presenter.removeVoucher()
            }

        }
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
    override fun showStockExceededError(productId: String, stockCount: Int) {
        showMaxPurchaseLimitDialog(stockCount)
    }

    override fun showCartSize(size: Int) {
        binding.tvToolbar.text=getString(R.string.title_cart_toolbar,size)
    }

    override fun navigateToCheckout(selectedItems: List<CartItem>, totalAmount: Double) {
        val intent= Intent(this,OrderActivity::class.java).apply {
            putExtra("SELECTED_ITEMS", ArrayList(selectedItems))
            putExtra("TOTAL_AMOUNT", totalAmount)

            putExtra("VOUCHER_ID",presenter.getCurrentVoucherId())
            putExtra("VOUCHER_DISCOUNT",presenter.getAppliledVoucherDiscount())
            putExtra("VOUCHER_DISCOUNT_TYPE",presenter.getAppliledVoucherDiscountType())
        }
        startActivity(intent)
    }

    override fun updateVoucherInfo(voucherDiscount: String?) {

        binding.priceReduceVoucher.apply {
            text = voucherDiscount
            visibility = if (voucherDiscount != null) View.VISIBLE else View.GONE
        }

        binding.chooseVoucherText.visibility = if (voucherDiscount != null) View.GONE else View.VISIBLE
    }

    override fun navigateHome() {
        startActivity(Intent(this,MainActivity::class.java))
    }

    @SuppressLint("MissingInflatedId")
    private fun showMaxPurchaseLimitDialog(maxCount: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.error_limit_stock_count_cart, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        val messageTextView = dialogView.findViewById<TextView>(R.id.tvMessage)
        messageTextView.text = getString(R.string.max_purchase_limit, maxCount)

        dialogView.findViewById<TextView>(R.id.tv_Agree).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun showCartItems(cartItems: List<CartItem>, products: Map<String, Product>) {
        if(cartItems.isEmpty()){
            binding.constraintLayoutShowCart.visibility=View.GONE
            binding.linearCart.visibility=View.GONE
            binding.linearLayoutNotItemCart.visibility= View.VISIBLE

        }
        else{
            binding.constraintLayoutShowCart.visibility=View.VISIBLE
            binding.linearCart.visibility=View.VISIBLE
            binding.linearLayoutNotItemCart.visibility= View.GONE
            cartItemAdapter.updateData(cartItems, products)
        }

    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        presenter.loadCartItems()
    }

    override fun updateSelectedItems(selectedIds: Set<String>) {
        cartItemAdapter.updateSelectedItems(selectedIds)
    }

    override fun updateTotalPrice(total: Double) {
        binding.cartTotalAmount.text= if(total>0){
            NumberFormatUtils.formatPrice(total)
        }
        else{
            getString(R.string.cart_total_amount_zero)
        }
    }

    override fun updatePurchaseBtnText(count: Int) {
        binding.btnBuy.text=getString(R.string.btn_buy,count)
    }

    override fun updateAllItemsSelection(isChecked: Boolean) {
        binding.cartCheckbox.isChecked=isChecked
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}