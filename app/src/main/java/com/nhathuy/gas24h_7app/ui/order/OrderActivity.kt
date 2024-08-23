package com.nhathuy.gas24h_7app.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.OrderItemAdapter
import com.nhathuy.gas24h_7app.data.model.Cart
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityOrderBinding
import javax.inject.Inject

class OrderActivity : AppCompatActivity(),OrderContract.View{
    private lateinit var binding:ActivityOrderBinding
    private lateinit var orderItemAdapter: OrderItemAdapter

    @Inject
    lateinit var presenter: OrderPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)

        val selectedItems = intent.getSerializableExtra("SELECTED_ITEMS") as? ArrayList<CartItem>
        val totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)

        setupToolbar()

        orderItemAdapter= OrderItemAdapter()
        binding.orderRec.apply {
            layoutManager=LinearLayoutManager(this@OrderActivity)
            adapter=orderItemAdapter
        }
        binding.orderTotalPrice.text="đ${String.format("%,.3f",totalAmount)}"

        presenter.attachView(this)
        selectedItems?.let {
            presenter.loadOrderItems(it)
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun showOrderItems(
        items: MutableList<CartItem>,
        products: MutableMap<String, Product>
    ) {
        orderItemAdapter.updateData(items,products)
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
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
}