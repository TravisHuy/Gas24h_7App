package com.nhathuy.gas24h_7app.ui.pending_payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityPendingPaymentBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.ui.cart.CartActivity
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject

class PendingPaymentActivity : AppCompatActivity() , PendingPaymentContract.View{

    private lateinit var binding:ActivityPendingPaymentBinding
    private lateinit var cartBadge: BadgeDrawable

    @Inject
    lateinit var presenter: PendingPaymentPresenter

    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)



        presenter.attachView(this)
        presenter.loadSuggestProducts()
        presenter.loadCartItemCount()

        setupImageSlider()
        setupCartBadge()
        setupCart()
        setupNavigateMain()
    }




    override fun backHome() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun setupCartBadge() {
        cartBadge = BadgeDrawable.create(this)
        cartBadge.isVisible = true
        cartBadge.backgroundColor = ContextCompat.getColor(this, R.color.badge_background_color)
        cartBadge.horizontalOffset = dpToPx(10)
        cartBadge.verticalOffset = dpToPx(2)
        val cartIcon = binding.detailCartItem
        BadgeUtils.attachBadgeDrawable(cartBadge, cartIcon, binding.detailCartItemContainer)
    }
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    override fun navigateCart() {
        startActivity(Intent(this,CartActivity::class.java))
        finish()
    }

    private fun setupNavigateMain() {
       binding.btnBack.setOnClickListener {
           backHome()
       }
        binding.btnHome.setOnClickListener {
            backHome()
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun setupSuggestProduct(products: List<Product>) {
        val suggestRecyclerView = findViewById<RecyclerView>(R.id.suggest_rec)
        suggestRecyclerView.layoutManager = GridLayoutManager(this,2)

        val adapter = ProductAdapter(products, object : ProductClickListener {
            override fun onProductClick(product: Product) {
                val intent = Intent(this@PendingPaymentActivity, DetailProductActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            }
        })
        suggestRecyclerView.adapter = adapter
    }

    override fun updateCartItemCount(count: Int) {
        if(count>0){
            cartBadge.isVisible = true
            cartBadge.number = count
        }
        else{
            cartBadge.isVisible=false
        }
    }
    private fun setupCart() {
        binding.detailCartItem.setOnClickListener {
            navigateCart()
        }
    }
    private fun setupImageSlider() {
        val listImage = listOf(
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT),
            SlideModel(R.drawable.image_test, ScaleTypes.FIT)
        )
        binding.imageSlider.setImageList(listImage)
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}