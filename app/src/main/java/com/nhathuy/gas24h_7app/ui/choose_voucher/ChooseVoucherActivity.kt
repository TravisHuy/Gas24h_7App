package com.nhathuy.gas24h_7app.ui.choose_voucher

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ChooseVoucherAdapter
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.databinding.ActivityChooseVoucherBinding
import javax.inject.Inject

class ChooseVoucherActivity : AppCompatActivity(),ChooseVoucherContract.View{

    private lateinit var binding:ActivityChooseVoucherBinding
    private lateinit var adapter: ChooseVoucherAdapter

    private var hasSelectedProducts: Boolean = false
    private var currentVoucherId: String? = null
    @Inject
    lateinit var presenter: ChooseVoucherPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChooseVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title=getString(R.string.title_choose_voucher)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)


        adapter = ChooseVoucherAdapter(this,
            onItemChecked = { voucherId,isChecked ->
                if (hasSelectedProducts) {
                    presenter.updateItemSelection(voucherId, isChecked)
                } else {
                    showNoProductSelectedToast()
                }
        })
        hasSelectedProducts = intent.getBooleanExtra("HAS_SELECTED_PRODUCTS", false)
        currentVoucherId = intent.getStringExtra("CURRENT_VOUCHER_ID")

        presenter.setHasSelectedProducts(hasSelectedProducts)
        presenter.setCurrentVoucherId(currentVoucherId)
        presenter.loadVouchers()

        setupRecyclerView()
        setupSearchView()
        setupAgreeBtn()

    }

    private fun setupAgreeBtn() {
        binding.btnAgree.setOnClickListener {
            presenter.onAgreeButtonClick()
        }
    }

    private fun showNoProductSelectedToast() {
        val toastView = layoutInflater.inflate(R.layout.dialog_error_voucher, null)
        val toast = Toast(applicationContext)
        toast.view = toastView
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun setupSearchView() {
        binding.btnInputApply.setOnClickListener {
            if (hasSelectedProducts) {
                val searchQuery = binding.edInputSearch.text.toString().trim()
                presenter.searchAndSelectFirstVoucher(searchQuery)
                hideKeyboard()
            } else {
                showNoProductSelectedToast()
            }
        }
        binding.edInputSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                presenter.searchVouchers(s.toString())
            }

        })
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edInputSearch.windowToken,0)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager= LinearLayoutManager(context)
            adapter = this@ChooseVoucherActivity.adapter
        }
        Log.d("ChooseVoucherActivity", "RecyclerView setup completed")
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

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun updateVoucherList(products: List<Voucher>, selectedVoucher: String?) {
        adapter.updateVouchers(products,selectedVoucher)
    }

    override fun updateTvDiscountPrice(discountInfo: String?) {
        binding.tvVoucherDiscount.apply {
            if(discountInfo!=null){
                text=discountInfo
                visibility=View.VISIBLE
            }
            else{
                visibility=View.GONE
            }
        }
    }

    override fun finishWithResult(selectedVoucher: Voucher?) {
        val intent = Intent()
        selectedVoucher?.let {
            intent.putExtra("SELECT_VOUCHER_ID", it.id)
            intent.putExtra("SELECT_VOUCHER_CODE", it.code)
            intent.putExtra("SELECTED_VOUCHER_DISCOUNT", it.discountValue)
            intent.putExtra("SELECTED_VOUCHER_TYPE", it.discountType.name)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}