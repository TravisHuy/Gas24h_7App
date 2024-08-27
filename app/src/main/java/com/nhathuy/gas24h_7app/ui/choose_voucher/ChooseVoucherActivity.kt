package com.nhathuy.gas24h_7app.ui.choose_voucher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
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
                presenter.updateItemSelection(voucherId,isChecked)
        })

        setupRecyclerView()
        presenter.loadVouchers()
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

    override fun updateVoucherList(products: List<Voucher>, selectedVouchers: Set<String>) {
        adapter.updateVouchers(products,selectedVouchers)
    }
}