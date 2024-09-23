package com.nhathuy.gas24h_7app.ui.order_information

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.adapter.PurchasedOrderProductItemAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityOrderInformationBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.fragment.hotline.HotlineFragment
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.purchased_order.PurchasedOrderActivity
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class OrderInformationActivity : AppCompatActivity(),OrderInformationContract.View {

    private lateinit var binding:ActivityOrderInformationBinding
    private lateinit var adapter: PurchasedOrderProductItemAdapter
    @Inject
    lateinit var presenter: OrderInformationPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val orderId = intent.getStringExtra("ORDER_ID") ?: return

        setupRecyclerView()
        presenter.loadUserInfo()
        presenter.loadSuggestProducts()
        presenter.loadOrder(orderId)

        setupToolbar()
        setupListeners()

        // handle cancel order
        binding.btnConfirmCancel.setOnClickListener {
            presenter.cancelOrder(orderId)
        }
    }




    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showUserInfo(user: User) {
        binding.orderNameCustomer.text=user.fullName
        binding.orderPhonenumber.text=user.phoneNumber
        binding.orderLocation.text=user.address
    }

    override fun showOrder(order: Order, products: Map<String, Product>) {
        binding.orderConfirmId.text = order.id
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        binding.orderStart.text = dateFormat.format(order.createdAt)

        binding.orderTotalPrice.text = NumberFormatUtils.formatPrice(order.totalAmount)

        adapter.updateOrderItems(order.items, products)
    }

    override fun setupSuggestProduct(products: List<Product>) {
        val suggestRecyclerView = findViewById<RecyclerView>(R.id.suggest_rec)
        suggestRecyclerView.layoutManager = GridLayoutManager(this,2)

        val adapter = ProductAdapter(products, object : ProductClickListener {
            override fun onProductClick(product: Product) {
                val intent = Intent(this@OrderInformationActivity, DetailProductActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            }
        })
        suggestRecyclerView.adapter = adapter
    }

    override fun navigateCall() {
        startActivity(Intent(this,HotlineFragment::class.java))
        finish()
    }

    override fun navigatePurchase() {
        startActivity(Intent(this,PurchasedOrderActivity::class.java))
        finish()
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
    private fun setupRecyclerView() {
        adapter = PurchasedOrderProductItemAdapter(onItemClicked = {
            productId->
        })
        binding.orderRec.apply {
            layoutManager= LinearLayoutManager(this@OrderInformationActivity)
            adapter=this@OrderInformationActivity.adapter
        }
    }
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
    private fun setupListeners() {
        binding.btnCall.setOnClickListener {
            navigateCall()
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}