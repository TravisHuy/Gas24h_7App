package com.nhathuy.gas24h_7app.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivitySearchBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import javax.inject.Inject

class SearchActivity : AppCompatActivity(),SearchContract.View {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter:ProductAdapter
    @Inject
    lateinit var presenter: SearchPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)
        setupViews()
        setupListeners()
    }
    private fun setupViews() {
        adapter = ProductAdapter(emptyList(), object : ProductClickListener {
            override fun onProductClick(product: Product) {
                val intent = Intent(this@SearchActivity, DetailProductActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                intent.putExtra("CATEGORY_ID", product.categoryId)
                startActivity(intent)
            }
        })
        binding.allSearchProductRecycler.layoutManager=GridLayoutManager(this,2)
        binding.allSearchProductRecycler.adapter=adapter
    }
    private fun setupListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { presenter.searchProducts(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally implement real-time search here
                return true
            }
        })
    }
    override fun showLoading() {
        binding.searchSwipeRefreshLayout.isRefreshing=true
    }

    override fun hideLoading() {
        binding.searchSwipeRefreshLayout.isRefreshing=false
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSearchResults(products: List<Product>) {
        adapter.updateData(products)
    }

//    override fun showRecentSearches(searches: List<Product>) {
//        TODO("Not yet implemented")
//    }
}