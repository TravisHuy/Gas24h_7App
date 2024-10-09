package com.nhathuy.gas24h_7app.admin.product_management.all_product

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.AllProductItemAdapter
import com.nhathuy.gas24h_7app.admin.product_management.edit_product.EditProductActivity
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityAllProductBinding
import com.nhathuy.gas24h_7app.util.Constants
import javax.inject.Inject

class AllProductActivity : AppCompatActivity(), AllProductContract.View {
    private lateinit var binding: ActivityAllProductBinding
    private lateinit var adapter: AllProductItemAdapter

    @Inject
    lateinit var presenter: AllProductPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)
        presenter.loadProducts()
        setupRecycler()
        setupSortingListeners()
        setupSwipeRefresh()
        setupSearchListener()
    }

    private fun setupRecycler() {
        binding.allProductRecycler.layoutManager = GridLayoutManager(this, 2)
        adapter = AllProductItemAdapter(onEditClick = {
            product ->presenter.onEditProductClicked(product)
        },
        onRemoveClick = {
            product ->
            presenter.onRemoveProductClicked(product)
        })
        binding.allProductRecycler.adapter = adapter
    }

    private fun setupSortingListeners() {
        binding.highPrice.setOnClickListener {
            presenter.sortProductsByPrice(true)
        }
        binding.lowPrice.setOnClickListener {
            presenter.sortProductsByPrice(false)
        }
        binding.highInventory.setOnClickListener {
            presenter.sortProductsByStock(true)
        }
        binding.lowInventory.setOnClickListener {
            presenter.sortProductsByStock(false)
        }
        binding.highBestSelling.setOnClickListener {
            presenter.sortProductsBySelling(true)
        }
        binding.lowBestSelling.setOnClickListener {
            presenter.sortProductsBySelling(false)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            presenter.loadProducts()
        }
    }

    private fun setupSearchListener() {
        binding.searchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val handler = Handler(Looper.getMainLooper())
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    presenter.searchProducts(s.toString())
                }, 300)
            }

        })
        binding.searchProduct.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                presenter.searchProducts(binding.searchProduct.text.toString())
                true
            } else {
                false
            }
        }

    }

    override fun showLoading() {
        binding.swipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProducts(products: List<Product>) {
        adapter.updateData(products)
    }

    override fun showEditProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java)
        intent.putExtra("product", product)
        startActivityForResult(intent, Constants.EDIT_PRODUCT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.EDIT_PRODUCT_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Product>("updatedProduct")?.let { updatedProduct ->
                presenter.editProduct(updatedProduct)
            }
        }
    }
    override fun showRemoveProductDialog(product: Product) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Remove Product")
            .setMessage("Are you sure you want to remove ${product.name}?")
            .setPositiveButton("Yes") { _, _ ->
                presenter.removeProduct(product.id)
            }
            .setNegativeButton("No", null)
            .show()

    }
}