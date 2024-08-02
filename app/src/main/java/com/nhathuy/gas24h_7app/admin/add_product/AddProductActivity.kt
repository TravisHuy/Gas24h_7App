package com.nhathuy.gas24h_7app.admin.add_product

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductImageAdapter
import com.nhathuy.gas24h_7app.databinding.ActivityAddProductBinding

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private val imageUris: MutableList<Uri> = mutableListOf()
    private lateinit var adapter: ProductImageAdapter
    private val PICK_IMAGE_REQUEST = 1
    private val MAX_IMAGE_COUNT = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        updateImageCount()
    }

    private fun setupRecyclerView() {
        adapter = ProductImageAdapter(imageUris) { position ->
            removeImage(position)
        }
        binding.productImagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.productImagesRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.addProductImage.setOnClickListener {
            if (imageUris.size < MAX_IMAGE_COUNT) {
                openImagePicker()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                imageUris.add(it)
                adapter.notifyItemInserted(imageUris.size - 1)
                updateImageCount()
            }
        }
    }

    private fun removeImage(position: Int) {
        if (position in 0 until imageUris.size) {
            imageUris.removeAt(position)
            adapter.notifyDataSetChanged()
            updateImageCount()
        }
    }

    private fun updateImageCount() {
        val imageCountText = "Thêm hình\nảnh(${imageUris.size}/$MAX_IMAGE_COUNT)"
        binding.addProductTvCount.text = imageCountText
        binding.addProductImage.isEnabled = imageUris.size < MAX_IMAGE_COUNT
        binding.addProductImage.alpha = if (imageUris.size < MAX_IMAGE_COUNT) 1.0f else 0.5f
    }
}