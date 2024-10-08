package com.nhathuy.gas24h_7app.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.admin.product_management.add_product.AddProductActivity
import com.nhathuy.gas24h_7app.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
            finish()
        }
    }
}