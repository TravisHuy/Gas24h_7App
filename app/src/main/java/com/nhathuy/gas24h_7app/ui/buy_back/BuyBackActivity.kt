package com.nhathuy.gas24h_7app.ui.buy_back

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.BuyBackAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityBuyBackBinding
import com.nhathuy.gas24h_7app.ui.cart.CartActivity
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import javax.inject.Inject

class BuyBackActivity : AppCompatActivity(),BuyBackContract.View,BuyBackAdapter.BuyBackClickListener {

    private lateinit var binding:ActivityBuyBackBinding
    private lateinit var adapter: BuyBackAdapter
    @Inject
    lateinit var presenter: BuyBackPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)

        presenter.attachView(this)
        presenter.loadOrders("DELIVERED")
        presenter.loadCartItemCount()
        setupRecyclerView()
        setupListeners()
    }


    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager= GridLayoutManager(this,2)

        adapter= BuyBackAdapter(listener = this)
        binding.recyclerView.adapter=adapter
    }
    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.btnCart.setOnClickListener {
            navigateCart()
        }
    }
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    override fun showLoading() {
        binding.loadingTextView.visibility=View.VISIBLE
        binding.contentScrollView.visibility=View.GONE
    }

    override fun hideLoading() {
        binding.loadingTextView.visibility=View.GONE
        binding.contentScrollView.visibility=View.VISIBLE
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showOrders(orders: List<Order>, products: Map<String, Product>) {
        adapter.updateOrderProducts(orders,products)
    }

    override fun onUpdateOrderStatus(orderId: String, newStatus: String) {
        presenter.updateOrderStatus(orderId,OrderStatus.valueOf(newStatus))
    }

    override fun updateCartItemCount(count: Int) {
        val cartBadge = binding.buyBackCartItemContainer.findViewById<TextView>(R.id.cart_badge)
        if(count>0){
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = if (count > 99) "99+" else count.toString()
        }
        else{
            cartBadge.visibility= View.GONE
        }
    }

    override fun navigateCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onProductClick(productId: String) {
        val intent = Intent(this, DetailProductActivity::class.java).apply {
            putExtra("PRODUCT_ID", productId)
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}