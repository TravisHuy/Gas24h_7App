package com.nhathuy.gas24h_7app.admin.voucher.all_product

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.databinding.ActivityVoucherAllProductBinding
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class VoucherAllProductActivity : AppCompatActivity(),VoucherAllContract.View {

    private lateinit var binding:ActivityVoucherAllProductBinding
    @Inject
    lateinit var presenter: VoucherAllPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVoucherAllProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)
        setupDiscountTypeDropdown()
        setupDatePickers()
        setupCreateVoucherBtn()
        setupVoucherCodeInput()
    }

    private fun setupDiscountTypeDropdown() {
        val discountTypes= arrayOf("Theo số tiền","Theo phần trăm")
        val adapter = ArrayAdapter(this,  android.R.layout.simple_dropdown_item_1line, discountTypes)
        binding.autocompleteDiscount.setAdapter(adapter)
        binding.autocompleteDiscount.setOnItemClickListener { _, _, position, _ ->
            when(position) {
                0 ->{
                    binding.voucherEdDiscount.inputType=InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    binding.voucherEdDiscount.hint="đ"
                }
                1->{
                    binding.voucherEdDiscount.inputType=InputType.TYPE_CLASS_NUMBER
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
        binding.codeVoucher.addTextChangedListener(object:TextWatcher{
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

    override fun showLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility=View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
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


}