package com.nhathuy.gas24h_7app.ui.all_voucher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.AllVoucherAdapter
import com.nhathuy.gas24h_7app.adapter.BuyBackAdapter
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.databinding.ActivityAllVoucherBinding
import javax.inject.Inject

class AllVoucherActivity : AppCompatActivity(),AllVoucherContract.View {
    private lateinit var binding : ActivityAllVoucherBinding
    private lateinit var adapter: AllVoucherAdapter
    @Inject
    lateinit var presenter:AllVoucherPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)



        setupRecycler()
        setupListeners()

        presenter.loadVouchers()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun setupRecycler() {
        binding.recyclerView.layoutManager= LinearLayoutManager(this)

        adapter= AllVoucherAdapter()
        binding.recyclerView.adapter=adapter
    }

    override fun showLoading() {
        binding.loadingTextView.visibility= View.VISIBLE
        binding.contentScrollView.visibility= View.GONE
    }

    override fun hideLoading() {
        binding.loadingTextView.visibility=View.GONE
        binding.contentScrollView.visibility=View.VISIBLE
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showVoucher(vouchers: List<Voucher>) {
        adapter.updateData(vouchers)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}