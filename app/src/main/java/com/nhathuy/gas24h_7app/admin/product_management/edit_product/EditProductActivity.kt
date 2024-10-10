package com.nhathuy.gas24h_7app.admin.product_management.edit_product

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.adapter.ProductImageAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.databinding.ActivityEditProductBinding
import com.nhathuy.gas24h_7app.util.Constants.MAX_IMAGE_COUNT
import com.nhathuy.gas24h_7app.util.Constants.PICK_COVER_IMAGE_REQUEST
import com.nhathuy.gas24h_7app.util.Constants.PICK_IMAGE_REQUEST
import javax.inject.Inject

class EditProductActivity : AppCompatActivity(), EditProductContract.View {

    private lateinit var binding: ActivityEditProductBinding
    private lateinit var adapter: ProductImageAdapter
    private lateinit var categoryAdapter: ArrayAdapter<String>

    @Inject
    lateinit var presenter: EditProductPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupRecyclerView()
        setupListeners()
        setupCategoryAdapter()

        val productId = intent.getStringExtra("PRODUCT_ID") ?: ""
        if (productId.isNotEmpty()) {
            presenter.loadProduct(productId)
        } else {
            showError("Invalid product ID")
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductImageAdapter(mutableListOf()) { position ->
            presenter.onImageRemoved(position)
        }
        binding.productImagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.productImagesRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.editProductImage.setOnClickListener {
            openImagePicker()
        }
        binding.editProduct.setOnClickListener {
            presenter.updateProduct()
        }
        binding.categoryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            presenter.getSelectedCategoryId(position)
        }
        binding.editProductCoverImage.setOnClickListener {
            openCoverImagePicker()
        }
        binding.deleteCoverImage.setOnClickListener {
            presenter.onCoverImageRemoved()
        }
    }

    private fun setupCategoryAdapter() {
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.categoryAutoComplete.setAdapter(categoryAdapter)
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun openCoverImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Cover Picture"), PICK_COVER_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            when(requestCode) {
                PICK_IMAGE_REQUEST -> {
                    presenter.onImageAdded(data.data!!)
                }
                PICK_COVER_IMAGE_REQUEST -> {
                    presenter.onCoverImageAdded(data.data!!)
                }
            }
        }
    }

    override fun showLoading() {
        binding.progressBarAddProduct.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBarAddProduct.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun updateImageCount(count: Int, max: Int) {
        binding.editProductTvCount.text = "Add Images\n($count/$max)"
    }

    override fun updateCoverImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(binding.productCoverImage)
            binding.cardViewCoverImage.visibility = View.VISIBLE
        } else {
            binding.cardViewCoverImage.visibility = View.GONE
        }
    }

    override fun updateCoverImageCount(count: Int, max: Int) {
        binding.tvEditProductCoverImage.text = "Add Cover\nImage ($count/$max)"
    }

    override fun addImageToAdapter(imageUrl: String) {
        adapter.addImage(Uri.parse(imageUrl))
    }

    override fun removeImageFromAdapter(position: Int) {
        adapter.removeImage(position)
    }

    override fun enableImageAddButton(enable: Boolean) {
        binding.editProductImage.isEnabled = enable
        binding.editProductImage.alpha = if (enable) 1.0f else 0.5f
    }

    override fun enableCoverImageAddButton(enable: Boolean) {
        binding.editProductCoverImage.isEnabled = enable
        binding.editProductCoverImage.alpha = if (enable) 1.0f else 0.5f
    }

    override fun setProductName(name: String) {
        binding.edEditProductName.setText(name)
    }

    override fun setProductPrice(price: String) {
        binding.edEditProductPrice.setText(price)
    }

    override fun setProductOfferPercentage(percentage: String) {
        binding.edEditProductOfferPercentage.setText(percentage)
    }

    override fun setProductCategory(category: String) {
        binding.categoryAutoComplete.setText(category, false)
    }

    override fun setProductDescription(description: String) {
        binding.edEditProductDescription.setText(description)
    }

    override fun setProductStockCount(count: String) {
        binding.edEditProductStockCount.setText(count)
    }

    override fun getProductName(): String = binding.edEditProductName.text.toString()
    override fun getSelectedCategoryPosition(): Int = binding.categoryAutoComplete.listSelection
    override fun getProductDescription(): String = binding.edEditProductDescription.text.toString()
    override fun getProductPrice(): String = binding.edEditProductPrice.text.toString()
    override fun getProductStockCount(): String = binding.edEditProductStockCount.text.toString()
    override fun getProductOfferPercentage(): String = binding.edEditProductOfferPercentage.text.toString()

    override fun updateCategoryList(categories: List<String>) {
        categoryAdapter.clear()
        categoryAdapter.addAll(categories)
        categoryAdapter.notifyDataSetChanged()
    }

    override fun showNameError(error: String?) {
        binding.layoutEditProductName.error = error
    }

    override fun showCategoryError(error: String?) {
        binding.editProductCategoryLayout.error = error
    }

    override fun showDescriptionError(error: String?) {
        binding.layoutEditProductDescription.error = error
    }

    override fun showPriceError(error: String?) {
        binding.layoutEditProductPrice.error = error
    }

    override fun showOfferPercentageError(error: String?) {
        binding.layoutEditProductOfferPercentage.error = error
    }

    override fun showProductStockCountError(error: String?) {
        binding.layoutEditProductStockCount.error = error
    }

    override fun showImageError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun clearImages() {
        adapter.clearImages()
        updateImageCount(0, MAX_IMAGE_COUNT)
    }

    override fun clearCoverImage() {
        binding.productCoverImage.setImageURI(null)
        binding.cardViewCoverImage.visibility = View.GONE
    }

    override fun populateProductData(product: Product) {
        setProductName(product.name)
        setProductPrice(product.price.toString())
        setProductOfferPercentage(product.offerPercentage.toString())
        setProductDescription(product.description)
        setProductStockCount(product.stockCount.toString())
        updateCoverImage(product.coverImageUrl)
        product.detailImageUrls.forEach { addImageToAdapter(it) }
        updateImageCount(product.detailImageUrls.size, MAX_IMAGE_COUNT)
    }

}