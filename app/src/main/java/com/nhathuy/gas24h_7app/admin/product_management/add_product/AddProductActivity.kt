package com.nhathuy.gas24h_7app.admin.product_management.add_product

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ProductImageAdapter
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.databinding.ActivityAddProductBinding
import com.nhathuy.gas24h_7app.util.Constants.MAX_IMAGE_COUNT
import com.nhathuy.gas24h_7app.util.Constants.PICK_COVER_IMAGE_REQUEST
import com.nhathuy.gas24h_7app.util.Constants.PICK_IMAGE_REQUEST
import javax.inject.Inject

class AddProductActivity : AppCompatActivity() , AddProductContract.View {

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var adapter: ProductImageAdapter
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private var selectedCategoryId: String? = null


    @Inject
    lateinit var presenter: AddProductPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupRecyclerView()
        setupListeners()
        setupCategoryAdapter()
        updateImageCount(0,MAX_IMAGE_COUNT)
        setDropdownHeight(binding.categoryAutoComplete,4)
    }

    private fun setDropdownHeight(categoryAutoComplete: AutoCompleteTextView, maxItems: Int) {
        categoryAutoComplete.post {
            val itemHeight=resources.getDimensionPixelSize(R.dimen.max_dropdown_height)
            categoryAutoComplete.dropDownHeight=itemHeight*maxItems
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
        binding.addProductImage.setOnClickListener {
            openImagePicker()
        }
        binding.addCategory.setOnClickListener {
            showDialogCategory()
        }
        binding.addProduct.setOnClickListener {
            presenter.addProduct()
        }
        binding.categoryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryId = presenter.getSelectedCategoryId(position)
            binding.addProductCategoryLayout.error = null
        }
        binding.addProductCoverImage.setOnClickListener {
            openCoverImagePicker()
        }

        binding.deleteCoverImage.setOnClickListener {
            presenter.onCoverImageRemoved()
        }
    }



    private fun setupCategoryAdapter() {
        categoryAdapter=
            ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.categoryAutoComplete.setAdapter(categoryAdapter)
    }

    private fun showDialogCategory() {
        val dialog =Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_category)

        val category_name=dialog.findViewById<TextInputEditText>(R.id.category_name)
        val btn_add_category=dialog.findViewById<Button>(R.id.btn_add_category)

        btn_add_category.setOnClickListener {
            presenter.addCategory(category_name.text.toString())
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations=R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.CENTER)
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
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val imageUri: Uri? = data.data
//            imageUri?.let {
//                imageUris.add(it)
//                adapter.notifyItemInserted(imageUris.size - 1)
//                updateImageCount()
//            }
//        }
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
           when(requestCode){
               PICK_IMAGE_REQUEST ->{
                   val imageUri: Uri? = data.data
                   imageUri?.let {
                       presenter.onImageAdded(it)
                   }
               }
               PICK_COVER_IMAGE_REQUEST->{
                   val imageUri: Uri? = data.data
                   imageUri?.let {
                       presenter.onCoverImageAdded(it)
                   }
               }
           }
        }
    }

    override fun showLoading() {
        binding.progressBarAddProduct.visibility= View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBarAddProduct.visibility= View.GONE
    }

//    private fun removeImage(position: Int) {
//        if (position in 0 until imageUris.size) {
//            imageUris.removeAt(position)
//            adapter.notifyDataSetChanged()
//            updateImageCount()
//        }
//    }

//    private fun updateImageCount() {
//        val imageCountText = "Thêm hình\nảnh(${imageUris.size}/$MAX_IMAGE_COUNT)"
//        binding.addProductTvCount.text = imageCountText
//        binding.addProductImage.isEnabled = imageUris.size < MAX_IMAGE_COUNT
//        binding.addProductImage.alpha = if (imageUris.size < MAX_IMAGE_COUNT) 1.0f else 0.5f
//    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun updateImageCount(count: Int, max: Int) {
        binding.addProductTvCount.text = "Thêm hình\nảnh(${count}/$max)"
    }

    override fun updateCoverImage(imageUrl: String) {
        binding.productCoverImage.setImageURI(Uri.parse(imageUrl))
        binding.cardViewCoverImage.visibility = View.VISIBLE
    }

    override fun updateCoverImageCount(count: Int, max: Int) {
        binding.tvAddProductCoverImage.text="Thêm anh\nbìa(${count}/$max)"
    }

    override fun addImageToAdapter(imageUrl: String) {
        adapter.addImage(Uri.parse(imageUrl))
    }

    override fun removeImageFromAdapter(position: Int) {
        adapter.removeImage(position)
    }

    override fun enableImageAddButton(enable: Boolean) {
        binding.addProductImage.isEnabled = enable
        binding.addProductImage.alpha = if (enable) 1.0f else 0.5f
    }

    override fun enableCoverImageAddButton(enable: Boolean) {
        binding.addProductCoverImage.isEnabled=enable
        binding.addProductCoverImage.alpha = if (enable) 1.0f else 0.5f
    }

    override fun clearInputFields() {
        binding.edAddProductName.text?.clear()
        binding.edAddProductPrice.text?.clear()
        binding.edAddProductOfferPercentage.text?.clear()
        binding.categoryAutoComplete.text?.clear()
        binding.edAddProductDescription.text?.clear()
        binding.edAddProductStockCount.text?.clear()
        clearImages()
        clearCoverImage()
        updateCoverImageCount(0,1)
    }
    override fun clearImages() {
        adapter.clearImages()
        updateImageCount(0, MAX_IMAGE_COUNT)
    }

    override fun clearCoverImage() {
        binding.productCoverImage.setImageURI(null)
        binding.cardViewCoverImage.visibility = View.GONE
    }

    override fun getProductName(): String = binding.edAddProductName.text.toString()
    override fun getProductPrice(): String = binding.edAddProductPrice.text.toString()
    override fun getProductOfferPercentage(): String = binding.edAddProductOfferPercentage.text.toString()
    override fun getProductCategory(): String =binding.categoryAutoComplete.text.toString()
    override fun getProductDescription(): String = binding.edAddProductDescription.text.toString()
    override fun getProductStockCount(): String = binding.edAddProductStockCount.text.toString()

    override fun updateCategoryList(categories: List<String>) {
        categoryAdapter.clear()
        categoryAdapter.addAll(categories)
        categoryAdapter.notifyDataSetChanged()
    }

    override fun getSelectedCategoryId(): String? = selectedCategoryId

    override fun showNameError(error: String?) {
        binding.layoutAddProductName.error = error
    }

    override fun showCategoryError(error: String?) {
        binding.addProductCategoryLayout.error = error
    }

    override fun showDescriptionError(error: String?) {
        binding.layoutAddProductDescription.error = error
    }

    override fun showPriceError(error: String?) {
        binding.layoutAddProductPrice.error = error
    }

    override fun showOfferPercentageError(error: String?) {
        binding.layoutAddProductOfferPercentage.error = error
    }

    override fun showProductStockCountError(error: String?) {
        binding.layoutAddProductStockCount.error = error
    }

    override fun showImageError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}