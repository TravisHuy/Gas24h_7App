package com.nhathuy.gas24h_7app.ui.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.CartItemAdapter
import com.nhathuy.gas24h_7app.data.model.Cart
import com.nhathuy.gas24h_7app.data.model.CartItem
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.databinding.ActivityCartBinding
import javax.inject.Inject

class CartActivity : AppCompatActivity(),CartContract.View {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartItemAdapter: CartItemAdapter

    @Inject
    lateinit var presenter: CartPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)

        cartItemAdapter = CartItemAdapter { productId, newQuantity ->
            presenter.updateCartItemQuantity(productId, newQuantity)
        }
        binding.recyclerViewCart.apply {
            layoutManager=LinearLayoutManager(context)
            adapter = cartItemAdapter
        }

        presenter.attachView(this)
        presenter.loadCartItems()
    }

    override fun showCartItems(cartItems: List<CartItem>, products: Map<String, Product>) {
        cartItemAdapter.updateData(cartItems, products)
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun updateCartItemQuantity(productId: String, newQuantity: Int) {
        presenter.loadCartItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}