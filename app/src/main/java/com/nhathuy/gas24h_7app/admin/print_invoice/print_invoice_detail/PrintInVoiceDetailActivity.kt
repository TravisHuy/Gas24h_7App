package com.nhathuy.gas24h_7app.admin.print_invoice.print_invoice_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.BillOrderAdapter
import com.nhathuy.gas24h_7app.adapter.InformationOderItemAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityPrintInVoiceDetailBinding
import javax.inject.Inject

class PrintInVoiceDetailActivity : AppCompatActivity() , PrintVoiceDetailContract.View {
    private lateinit var binding:ActivityPrintInVoiceDetailBinding
    private lateinit var adapter:BillOrderAdapter
    @Inject
    lateinit var presenter: PrintVoiceDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintInVoiceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val orderIds = intent.getStringArrayListExtra("ORDER_IDS") ?: emptyList()

        setupRecycler()

        presenter.printVoices(orderIds)
        Log.d("PrintInVoiceActivity","${orderIds.size}")
        binding.printSwipeRefreshLayout.setOnRefreshListener {
            presenter.printVoices(orderIds)
        }
    }

    private fun setupRecycler() {
        adapter = BillOrderAdapter()
        binding.printInVoiceRec.layoutManager = LinearLayoutManager(this)
        binding.printInVoiceRec.adapter = adapter
    }
    override fun showLoading() {
        binding.printSwipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.printSwipeRefreshLayout.isRefreshing = false
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showOrders(
        orders: List<Order>,
        products: Map<String, Product>,
        users: Map<String, User>
    ) {
        adapter.updateData(orders, users, products)
    }
}