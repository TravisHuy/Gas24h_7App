package com.nhathuy.gas24h_7app.admin.order.pending_confirmation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.PendingConfirmationAdapter
import com.nhathuy.gas24h_7app.adapter.PurchasedProductAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityPendingConfirmationBinding
import javax.inject.Inject

class PendingConfirmationActivity : AppCompatActivity(),PendingConfirmationContract.View {

    private lateinit var binding:ActivityPendingConfirmationBinding
    private lateinit var adapter: PendingConfirmationAdapter
    @Inject
    lateinit var presenter: PendingConfirmationPresenter

    private val selectedOrders = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPendingConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)

        setupRec()
        setupListeners()
        setupSearchView()

        presenter.attachView(this)
        presenter.loadOrders("PENDING")

    }

    private fun setupSearchView() {
        binding.searchEditText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                presenter.searchOrders(s.toString())
            }

        })
    }

    private fun setupListeners() {
        binding.selectAllCheckbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.toggleSelectAll(isChecked)
        }
        binding.btnCancel.setOnClickListener {
            presenter.clearSelection()
        }
        binding.btnConfirm.setOnClickListener {
            presenter.confirmSelectOrders()
        }
    }

    private fun setupRec() {
        adapter= PendingConfirmationAdapter(onItemChecked = {
                orderId,isChecked ->
            presenter.updateItemSelection(orderId,isChecked)
        }, onItemClicked = {
            orderId ->

        })
        binding.recyclerViewOrder.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewOrder.adapter = adapter
    }

    override fun showLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility=View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showOrders(orders: List<Order>, products: Map<String, Product>,users: Map<String,User>) {
        if(orders.isEmpty()){
            binding.linearLayoutNoOrders.visibility=View.VISIBLE
            binding.recyclerViewOrder.visibility=View.GONE
        }
        else{
            binding.linearLayoutNoOrders.visibility=View.GONE
            binding.recyclerViewOrder.visibility=View.VISIBLE
            adapter.updateData(orders, users, products, selectedOrders)
        }
    }

    override fun updateOrderList(orders: List<Order>, selectedOrders: MutableSet<String>) {
        this.selectedOrders.clear()
        this.selectedOrders.addAll(selectedOrders)
        adapter.updateSelectedItems(selectedOrders)
    }

    override fun updateSelectAllCheckbox(isAllSelected: Boolean) {
        binding.selectAllCheckbox.isChecked = isAllSelected
    }

    override fun clearSelectItems() {
        adapter.clearSelection()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}