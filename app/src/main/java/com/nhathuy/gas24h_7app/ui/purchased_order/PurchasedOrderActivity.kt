package com.nhathuy.gas24h_7app.ui.purchased_order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.OrderClickListener
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.adapter.PurchasedOrderItemAdapter
import com.nhathuy.gas24h_7app.adapter.PurchasedProductAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityPurchasedOrderBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.order_information.OrderInformationActivity
import com.nhathuy.gas24h_7app.ui.order_information.OrderInformationContract
import javax.inject.Inject

class PurchasedOrderActivity : AppCompatActivity(),PurchasedOrderContract.View, OrderClickListener {

    private lateinit var binding:ActivityPurchasedOrderBinding
    private lateinit var adapter:PurchasedOrderItemAdapter

    @Inject
    lateinit var presenter: PurchasedOrderPresenter


    private val statusMap = mapOf(
        "PENDING" to "Đặt hàng thành công",
        "PROCESSING" to "Đã tiếp nhận",
        "SHIPPED" to "Đang vận chuyển",
        "DELIVERED" to "Đã giao",
        "CANCELLED" to "Đã hủy"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPurchasedOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)

        setupRec()
        setupTabLayout()

        presenter.attachView(this)
        presenter.loadOrders("PENDING")
        presenter.loadSuggestProducts()
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

    override fun showOrders(orders: List<Order>,products:Map<String,Product>) {
        if(orders.isEmpty()){
            binding.linearLayoutNoOrders.visibility=View.VISIBLE
            binding.purchasedRec.visibility=View.GONE
        }
        else{
            binding.linearLayoutNoOrders.visibility=View.GONE
            binding.purchasedRec.visibility=View.VISIBLE
            adapter.updateOrders(orders)
            adapter.updateProducts(products)
        }
    }

    override fun setupSuggestProduct(products: List<Product>) {
        val suggestRecyclerView = findViewById<RecyclerView>(R.id.suggest_rec)
        suggestRecyclerView.layoutManager = GridLayoutManager(this,2)

        val adapter = ProductAdapter(products, object : ProductClickListener {
            override fun onProductClick(product: Product) {
                val intent = Intent(this@PurchasedOrderActivity, DetailProductActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            }
        })
        suggestRecyclerView.adapter = adapter
    }

    override fun showOrderStatusUpdateSuccess() {
        Toast.makeText(this, "Order status updated successfully", Toast.LENGTH_SHORT).show()
//
    }

    private fun setupRec() {
        adapter=PurchasedOrderItemAdapter(listener = this)
        binding.purchasedRec.layoutManager = LinearLayoutManager(this)
        binding.purchasedRec.adapter = adapter
    }
    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val status = statusMap.entries.elementAt(tab?.position ?: 0).key
                presenter.loadOrders(status)
                (binding.purchasedRec.adapter as? PurchasedOrderItemAdapter)?.updateStatus(status)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    override fun onOrderClick(orderId: String) {
        val intent = Intent(this,OrderInformationActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        startActivity(intent)
    }

    override fun onProductClick(orderId: String, productId: String) {
        val intent = Intent(this, OrderInformationActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        intent.putExtra("PRODUCT_ID", productId)
        startActivity(intent)
    }

    override fun onUpdateOrderStatus(orderId: String, newStatus: String) {
        presenter.updateOrderStatus(orderId,OrderStatus.valueOf(newStatus))
    }
}