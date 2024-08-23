package com.nhathuy.gas24h_7app.admin.voucher.detail_product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.ActivityVoucherAllProductBinding
import com.nhathuy.gas24h_7app.databinding.ActivityVoucherDetailProductBinding

class VoucherDetailProductActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVoucherDetailProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVoucherDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}