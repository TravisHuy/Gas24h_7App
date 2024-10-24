package com.nhathuy.gas24h_7app.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivitySearchBinding
import com.nhathuy.gas24h_7app.databinding.DialogReviewStarsBinding
import com.nhathuy.gas24h_7app.databinding.DialogSearchReviewBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
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
                query?.let {
                    presenter.searchProducts(it)
                    binding.chipRelevance.isChecked = true
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    presenter.clearSearch()
                }
                return true
            }
        })
        binding.btnBack.setOnClickListener {
            navigateHome()
        }
        binding.chipRelevance.setOnClickListener {
            presenter.sortByRelevance()
            binding.chipRelevance.isChecked = true
        }
        binding.chipBestSeller.setOnClickListener {
            presenter.sortByBestSeller()
            binding.chipRelevance.isChecked = false
        }
        binding.highPrice.setOnClickListener {
            presenter.sortByHighPrice()
            binding.chipRelevance.isChecked = false
        }
        binding.lowPrice.setOnClickListener {
            presenter.sortByLowPrice()
            binding.chipRelevance.isChecked = false
        }
        binding.chipReview.setOnClickListener {
            showDialogStar()
            binding.chipRelevance.isChecked = false
        }
        binding.searchView.setOnCloseListener {
            presenter.clearSearch()
            true
        }
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

    override fun showDialogStar() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val binding = DialogSearchReviewBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        val ratingCheckboxes = listOf(
            binding.checkbox5Star to 5f,
            binding.checkbox4Star to 4f,
            binding.checkbox3Star to 3f,
            binding.checkbox2Star to 2f,
            binding.checkbox1Star to 1f
        )

        ratingCheckboxes.forEach { (checkbox, rating) ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // Uncheck other checkboxes
                    ratingCheckboxes.forEach { (otherCheckbox, _) ->
                        if (otherCheckbox != buttonView) {
                            otherCheckbox.isChecked = false
                        }
                    }
                    // Apply filter
                    presenter.filterByRating(rating)
                    bottomSheetDialog.dismiss()
                }
            }
        }

        listOf(
            binding.rating5Star,
            binding.rating4Star,
            binding.rating3Star,
            binding.rating2Star,
            binding.rating1Star
        ).forEach { ratingBar ->
            ratingBar.isEnabled = false
        }

        bottomSheetDialog.show()
    }

    override fun clearSearchResults() {
        adapter.updateData(emptyList())
        binding.chipRelevance.isChecked = true
        // Reset other filter chips if needed
        binding.chipBestSeller.isChecked = false
        binding.chipReview.isChecked = false
    }

    override fun navigateHome() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

//    override fun showRecentSearches(searches: List<Product>) {
//        TODO("Not yet implemented")
//    }
}