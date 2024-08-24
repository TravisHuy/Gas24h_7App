package com.nhathuy.gas24h_7app.admin.voucher.detail_product

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.VoucherProductAdapter
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.databinding.ActivityVoucherAllProductBinding
import com.nhathuy.gas24h_7app.databinding.ActivityVoucherDetailProductBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class VoucherDetailProductActivity : AppCompatActivity(),VoucherDetailContract.View {
    private lateinit var binding:ActivityVoucherDetailProductBinding

    private lateinit var voucherAdapter:VoucherProductAdapter
    private lateinit var dialog:Dialog
    @Inject
    lateinit var presenter:VoucherDetailPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVoucherDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        voucherAdapter=VoucherProductAdapter(
            onItemChecked = {
                    productId,isChecked ->
                presenter.updateItemSelection(productId,isChecked)
            }
        )
        setupDiscountTypeDropdown()
        setupDatePickers()
        setupCreateVoucherBtn()
        setupVoucherCodeInput()
        setupAddProduct()

    }




    private fun setupDiscountTypeDropdown() {
        val discountTypes= arrayOf("Theo số tiền","Theo phần trăm")
        val adapter = ArrayAdapter(this,  android.R.layout.simple_dropdown_item_1line, discountTypes)
        binding.autocompleteDiscount.setAdapter(adapter)
        binding.autocompleteDiscount.setOnItemClickListener { _, _, position, _ ->
            when(position) {
                0 ->{
                    binding.voucherEdDiscount.inputType= InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    binding.voucherEdDiscount.hint="đ"
                }
                1->{
                    binding.voucherEdDiscount.inputType= InputType.TYPE_CLASS_NUMBER
                    binding.voucherEdDiscount.hint="%"
                }
            }
        }
    }

    private fun setupDatePickers() {
        binding.linearLayoutStartTime.setOnClickListener {
            showDateTimePicker(true)
        }

        binding.linearLayoutEndTime.setOnClickListener {
            showDateTimePicker(false)
        }
    }

    private fun showDateTimePicker(isStartDate: Boolean) {
        val currentDate =Calendar.getInstance()

        DatePickerDialog(this,{_,year,month,day ->
            if(isStartDate){
                presenter.onStartDateSelected(year, month, day)
            }
            else{
                presenter.onEndDateSelected(year, month, day)
            }
            TimePickerDialog(this,{_,hourOfDay,minute ->
                if(isStartDate){
                    presenter.onStartTimeSelected(hourOfDay, minute)
                }
                else{
                    presenter.onEndTimeSelected(hourOfDay, minute)
                }
            },currentDate.get(Calendar.HOUR_OF_DAY),currentDate.get(Calendar.MINUTE),true).show()
        },currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun setupVoucherCodeInput() {
        binding.codeVoucher.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val filtered =it.toString().uppercase().replace(Regex("[^A-Z0-9]"), "")
                    if (filtered != it.toString()) {
                        binding.codeVoucher.setText(filtered)
                        binding.codeVoucher.setSelection(filtered.length)
                    }
                }
            }

        })
    }

    private fun setupCreateVoucherBtn() {
        binding.btnConfirm.setOnClickListener {
            val voucher=createVoucherFromInput()
            presenter.createVoucher(voucher)
        }
    }

    private fun createVoucherFromInput(): Voucher {
        return Voucher(
            id = UUID.randomUUID().toString(),
            code = binding.codeVoucher.text.toString(),
            discountType = if (binding.autocompleteDiscount.text.toString() == "Theo phần trăm")
                DiscountType.PERCENTAGE else DiscountType.FIXED_AMOUNT,
            discountValue = binding.voucherEdDiscount.text.toString().toDouble(),
            minOrderAmount = binding.voucherMinimumPrice.text.toString().toDouble(),
            maxUsage = binding.voucherTotalNumberOfUse.text.toString().toInt(),
            maxUsagePreUser = binding.voucherMaximumWithPerson.text.toString().toInt(),
            startDate = presenter.getStartDate().time,
            endDate = presenter.getEndDate().time,
            isActive = true,
            isForAllProducts = true
        )
    }
    private fun setupAddProduct() {
        binding.voucherAddProduct.setOnClickListener {
            showDialogAddProduct()
        }
    }

    private fun showDialogAddProduct() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.voucher_add_product_item)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewProducts)
        val searchEditText = dialog.findViewById<TextInputEditText>(R.id.searchEditText)
        val searchButton = dialog.findViewById<MaterialButton>(R.id.btn_search)
        val resetButton = dialog.findViewById<MaterialButton>(R.id.btn_input_again)
        val cancelButton = dialog.findViewById<MaterialButton>(R.id.btn_cancel)
        val confirmButton = dialog.findViewById<MaterialButton>(R.id.btn_confirm)
        val closeButton = dialog.findViewById<ImageView>(R.id.btn_close)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = voucherAdapter

        searchButton.setOnClickListener {
            presenter.searchProducts(searchEditText.text.toString())
        }

        resetButton.setOnClickListener {
            searchEditText.text?.clear()
            presenter.loadProducts()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        confirmButton.setOnClickListener {
            updateSelectedProducts()
            dialog.dismiss()
        }
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        setupSelectAllCheckbox()

        presenter.loadProducts()

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations=R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.CENTER)
    }

    private fun updateSelectedProducts() {

    }


    override fun showLoading() {
        binding.progressBar.visibility= View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility= View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun clearFields() {
        binding.codeVoucher.text?.clear()
        binding.voucherEdDiscount.text?.clear()
        binding.voucherMinimumPrice.text?.clear()
        binding.voucherTotalNumberOfUse.text?.clear()
        binding.voucherMaximumWithPerson.setText("1")

        // Reset dates
        presenter.resetDates()
    }

    override fun updateDateTimeDisplay(calendar: Calendar, isStartDate: Boolean) {
        val dateFormat= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        if (isStartDate) {
            binding.voucherDateStartTime.text = dateFormat.format(calendar.time)
            binding.voucherHourStartTime.text = timeFormat.format(calendar.time)
        } else {
            binding.voucherDateEndTime.text = dateFormat.format(calendar.time)
            binding.voucherHourEndTime.text = timeFormat.format(calendar.time)
        }
    }

    override fun updateProductList(products: List<Product>, selectedItemIds: MutableSet<String>) {
        voucherAdapter.updateProducts(products, selectedItemIds)
    }

    override fun updateSelectAllCheckbox(isAllSelected: Boolean) {
        dialog.findViewById<CheckBox>(R.id.selectAllCheckbox).isChecked=isAllSelected
    }
    private fun setupSelectAllCheckbox() {
        dialog.findViewById<CheckBox>(R.id.selectAllCheckbox).setOnCheckedChangeListener { _, isChecked ->
            presenter.toggleSelectAll(isChecked)
        }
    }
}