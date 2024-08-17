package com.nhathuy.gas24h_7app.ui.detail_product

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.internal.ViewUtils.dpToPx
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.databinding.ActivityDetailProductBinding
import com.nhathuy.gas24h_7app.fragment.hotline.HotlineFragment
import com.nhathuy.gas24h_7app.ui.cart.CartActivity
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject

class DetailProductActivity : AppCompatActivity() ,DetailProductContract.View{

    private lateinit var binding:ActivityDetailProductBinding
    private var isDescriptionExpanded = false

    private lateinit var currentProduct: Product
    private lateinit var cartBadge:BadgeDrawable
    @Inject
    lateinit var presenter: DetailProductPresenter
    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setupImageSlider()
//        setupSuggestProducts()
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val productId = intent.getStringExtra("PRODUCT_ID")?:""
        val categoryId = intent.getStringExtra("CATEGORY_ID")?:""

        presenter.loadProductDetails(productId)
        presenter.loadSuggestProducts(categoryId)

        setupDescriptionToggle()
        setupBottomNavigation()
        setupCartBadge()
        setupCart()
        backHome()
        setupHotline()
        presenter.loadCartItemCount()
    }

    private fun setupHotline() {
        binding.detailCallHotline.setOnClickListener {
            navigateHotline()
        }
    }

    private fun setupCart() {
        binding.detailCartItem.setOnClickListener {
            navigateCart()
        }
    }

    private fun setupDescriptionToggle() {
        binding.layoutDescriptionProduct.seeMoreLayout.setOnClickListener {
            isDescriptionExpanded=!isDescriptionExpanded
            updateDescriptionVisibility()
        }
    }

    private fun updateDescriptionVisibility() {
        with(binding.layoutDescriptionProduct){
            if(isDescriptionExpanded){
                shortDescription.visibility=View.GONE
                fullDescription.visibility=View.VISIBLE
                seeMoreButton.text = "Thu gọn"
                seeMoreIcon.setImageResource(R.drawable.ic_up)
            }
            else{
                shortDescription.visibility = View.VISIBLE
                fullDescription.visibility = View.GONE
                seeMoreButton.text = "Xem thêm"
                seeMoreIcon.setImageResource(R.drawable.ic_down)
            }
        }
    }

    private fun setupSuggestProducts() {
//        // Thiết lập phần sản phẩm gợi ý
//        val suggestLayout = binding.layoutSuggestProduct.root
//        val suggestRecyclerView = suggestLayout.findViewById<RecyclerView>(R.id.suggest_rec)
//
//        // Tạo danh sách sản phẩm gợi ý
//        val suggestProducts = listOf(
//            SuggestProduct(R.drawable.test_image, "Serum red peel da So Natural", "115.000đ", "Đã bán 1,9k"),
//            SuggestProduct(R.drawable.test_image, "Khô Cá Chỉ Vàng Tẩm Gia Vị", "68.000đ", "Đã bán 2k"),
//            SuggestProduct(R.drawable.test_image, "CAMERA IP YOOSEE", "172.000đ", "Đã bán 152,5k"),
//            SuggestProduct(R.drawable.test_image, "Tai nghe HiFi Audio", "7.989đ", "Đã bán 3,9k")
//        )
//
//        // Thiết lập RecyclerView với GridLayoutManager 2 cột
//        val layoutManager = GridLayoutManager(this, 2)
//        suggestRecyclerView.layoutManager = layoutManager
//        suggestRecyclerView.adapter = SuggestProductAdapter(suggestProducts)


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
    class SuggestProductAdapter(private val products: List<SuggestProduct>) :
        RecyclerView.Adapter<SuggestProductAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.productImage)
            val nameText: TextView = view.findViewById(R.id.productName)
            val priceText: TextView = view.findViewById(R.id.productPrice)
            val soldText: TextView = view.findViewById(R.id.productSold)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.suggest_product_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = products[position]
            holder.imageView.setImageResource(product.image)
            holder.nameText.text = product.name
//            holder.priceText.text = product.price
            holder.soldText.text = product.sold
        }

        override fun getItemCount() = products.size
    }
    data class SuggestProduct(
        val image: Int,
        val name: String,
        val price: Double,
        val sold: String
    )

    override fun showLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility=View.GONE
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showError(message:String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showProductDetails(product: Product) {
        currentProduct=product
        binding.detailProductName.text=product.name
        binding.layoutDescriptionProduct.shortDescription.text=product.description
        binding.layoutDescriptionProduct.fullDescription.text=product.description


        val originalPrice = product.price
        val discountedPrice = product.getDiscountedPrice()

        if (product.offerPercentage > 0.0) {
            binding.priceOriginalProduct.paintFlags = binding.priceOriginalProduct.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.priceOriginalProduct.text  = String.format("đ%.2f", originalPrice)
            binding.priceReduceProduct.text = String.format("đ%.2f", discountedPrice)
            binding.discountPercentage.text= String.format("-%.0f%%", product.offerPercentage)
        } else {
            binding.priceReduceProduct.text = String.format("đ%.2f", originalPrice)

            binding.priceOriginalProduct.visibility=View.GONE
            binding.discountPercentage.visibility=View.GONE
        }
    }

    override fun setupImageSlider(detailImages: List<String>) {
        val imageList= detailImages.map {
            imageUrl->
            SlideModel(imageUrl,ScaleTypes.FIT)
        }
        binding.imageSlider.setImageList(imageList)
    }

    override fun setupSuggestProduct(products: List<Product>) {
//        val layoutManager = GridLayoutManager(this, 2)
//        binding.layoutSuggestProduct.root.findViewById<RecyclerView>(R.id.suggest_rec).apply {
//            this.layoutManager = layoutManager
//            adapter = SuggestProductAdapter(products.map { product ->
//                SuggestProduct(
//                    image = R.drawable.placeholder_image, // Sử dụng hình placeholder nếu không có hình
//                    name = product.name,
//                    price = product.price,
//                    sold = product.soldCount // Giả sử `soldCount` là một thuộc tính của `Product`
//                )
//            })
//        }
    }

    override fun backHome() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun setupBottomNavigation() {
        binding.bottomNavigation.findViewById<View>(R.id.detail_cart_plus).setOnClickListener {
            showAddToCartDialog()
        }
    }

    override fun updateCartItemCount(count: Int) {
        if (count > 0) {
            cartBadge.isVisible = true
            cartBadge.number = count
        } else {
            cartBadge.isVisible = false
        }
    }
    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun setupCartBadge() {
        cartBadge=BadgeDrawable.create(this)
        cartBadge.isVisible=false
        cartBadge.backgroundColor=ContextCompat.getColor(this,R.color.badge_background_color)
        cartBadge.horizontalOffset = dpToPx(10)
        cartBadge.verticalOffset= dpToPx(2)
        val cartIcon = binding.detailCartItem
        BadgeUtils.attachBadgeDrawable(cartBadge,cartIcon,binding.detailCartItemContainer)
    }

    override fun navigateCart() {
        startActivity(Intent(this,CartActivity::class.java))
    }

    override fun navigateHotline() {
        val intent = Intent(this,MainActivity::class.java).apply {
            putExtra("navigate_to","hotline")
        }
        startActivity(intent)
        finish()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    private fun showAddToCartDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_product_to_cart_layout,null)
        bottomSheetDialog.setContentView(view)

        // Initialize views in the dialog
        val productImage = view.findViewById<ImageView>(R.id.product_cover_image)
        val priceText = view.findViewById<TextView>(R.id.price_text)
        val stockText = view.findViewById<TextView>(R.id.stock_text)
        val quantityEdit = view.findViewById<EditText>(R.id.quantity_edit)
        val decreaseBtn = view.findViewById<ImageView>(R.id.decrease_btn)
        val increaseBtn = view.findViewById<ImageView>(R.id.increase_btn)
        val addToCartBtn = view.findViewById<Button>(R.id.btn_add_to_cart)
        val closeBtn = view.findViewById<ImageView>(R.id.close_btn)

        val price = if(currentProduct.offerPercentage<0){
            currentProduct.getDiscountedPrice()
        }
        else{
            currentProduct.price
        }
        priceText.text=String.format("đ%.2f",price)
        stockText.text=currentProduct.stockCount.toString()

        Glide.with(this)
            .load(currentProduct.coverImageUrl)
            .into(productImage)

        decreaseBtn.setOnClickListener {
            var quantity = quantityEdit.text.toString().toIntOrNull()?:1
            if(quantity>1){
                quantity--
                quantityEdit.setText(quantity.toString())
            }
        }
        increaseBtn.setOnClickListener {
            var quantity = quantityEdit.text.toString().toIntOrNull()?:1
            quantity++
            quantityEdit.setText(quantity.toString())

        }
        addToCartBtn.setOnClickListener {
            var quantity = quantityEdit.text.toString().toIntOrNull()?:1
            presenter.addToCart(productId = currentProduct.id, quantity = quantity, price = price)
            bottomSheetDialog.dismiss()
        }

        closeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}