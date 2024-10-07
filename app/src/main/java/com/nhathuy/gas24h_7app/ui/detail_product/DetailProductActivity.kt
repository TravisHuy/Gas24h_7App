package com.nhathuy.gas24h_7app.ui.detail_product

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.widget.addTextChangedListener
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
import com.nhathuy.gas24h_7app.adapter.ProductAdapter
import com.nhathuy.gas24h_7app.adapter.ReviewAdapter
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.databinding.ActivityDetailProductBinding
import com.nhathuy.gas24h_7app.databinding.ReviewProductBinding
import com.nhathuy.gas24h_7app.fragment.categories.ProductClickListener
import com.nhathuy.gas24h_7app.fragment.hotline.HotlineFragment
import com.nhathuy.gas24h_7app.ui.all_review.AllReviewActivity
import com.nhathuy.gas24h_7app.ui.cart.CartActivity
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import javax.inject.Inject

class DetailProductActivity : AppCompatActivity(), DetailProductContract.View {

    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var reviewBinding: ReviewProductBinding
    private var isDescriptionExpanded = false

    private lateinit var currentProduct: Product
    private lateinit var cartBadge: BadgeDrawable

    private lateinit var adapter: ReviewAdapter

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var productId: String?= null
    @Inject
    lateinit var presenter: DetailProductPresenter


    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reviewBinding = ReviewProductBinding.bind(binding.layoutReviewProduct.root)

        // Inject dependencies and attach the presenter to the view
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        //Inject dependencies and attach the presenter to the view
        productId = intent.getStringExtra("PRODUCT_ID") ?: ""
        val categoryId = intent.getStringExtra("CATEGORY_ID") ?: ""

        // Load product details, suggestions , cartItem count
        presenter.loadProductDetails(productId!!)
        presenter.loadSuggestProducts(productId!!)
        presenter.loadCartItemCount()
        presenter.loadReviews(productId!!)
        presenter.loadProductSoldCount(productId!!)

        //set up UI components
        setupDescriptionToggle()
        setupBottomNavigation()
        setupCartBadge()
        setupCart()
        backHome()
        setupHotline()
        setupRecReview()
        setupListeners()
    }

    // set up the hotline button click listener
    private fun setupHotline() {
        binding.detailCallHotline.setOnClickListener {
            navigateHotline()
        }
    }

    // set up the cart button click listener
    private fun setupCart() {
        binding.detailCartItem.setOnClickListener {
            navigateCart()
        }
    }

    //toggle product description visibility
    private fun setupDescriptionToggle() {
        binding.layoutDescriptionProduct.seeMoreLayout.setOnClickListener {
            isDescriptionExpanded = !isDescriptionExpanded
            updateDescriptionVisibility()
        }
    }

    // Update description visibility based on the toggle state
    private fun updateDescriptionVisibility() {
        with(binding.layoutDescriptionProduct) {
            if (isDescriptionExpanded) {
                shortDescription.visibility = View.GONE
                fullDescription.visibility = View.VISIBLE
                seeMoreButton.text = "Thu gọn"
                seeMoreIcon.setImageResource(R.drawable.ic_up)
            } else {
                shortDescription.visibility = View.VISIBLE
                fullDescription.visibility = View.GONE
                seeMoreButton.text = "Xem thêm"
                seeMoreIcon.setImageResource(R.drawable.ic_down)
            }
        }
    }
    private fun setupRecReview() {
        val reviewRecyclerView = findViewById<RecyclerView>(R.id.recycler_reviews)
        reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter()
        reviewRecyclerView.adapter = adapter
    }
    private fun setupListeners() {
        val layoutReviewProduct = binding.layoutReviewProduct
        layoutReviewProduct.tvShowAll.setOnClickListener {
            navigateAllReviews()
        }
        layoutReviewProduct.tvAllCountReview.setOnClickListener {
            navigateAllReviews()
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

    // MVP View method implementations
    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProductDetails(product: Product) {
        currentProduct = product
        binding.detailProductName.text = product.name
        binding.layoutDescriptionProduct.shortDescription.text = product.description
        binding.layoutDescriptionProduct.fullDescription.text = product.description


        val originalPrice = product.price
        val discountedPrice = product.getDiscountedPrice()

        if (product.offerPercentage > 0.0) {
            binding.priceOriginalProduct.paintFlags =
                binding.priceOriginalProduct.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.priceOriginalProduct.text = NumberFormatUtils.formatPrice(originalPrice)
            binding.priceReduceProduct.text = NumberFormatUtils.formatPrice(discountedPrice)
            binding.discountPercentage.text = String.format("-%.0f%%", product.offerPercentage)
        } else {
            binding.priceReduceProduct.text = NumberFormatUtils.formatPrice(originalPrice)
            binding.priceOriginalProduct.visibility = View.GONE
            binding.discountPercentage.visibility = View.GONE
        }
    }

    override fun setupImageSlider(detailImages: List<String>) {
        val imageList = detailImages.map { imageUrl ->
            SlideModel(imageUrl, ScaleTypes.FIT)
        }
        binding.imageSlider.setImageList(imageList)
    }

    override fun setupSuggestProduct(products: List<Product>) {
        val suggestRecyclerView = findViewById<RecyclerView>(R.id.suggest_rec)
        suggestRecyclerView.layoutManager = GridLayoutManager(this,2)

        val adapter = ProductAdapter(products, object : ProductClickListener {
            override fun onProductClick(product: Product) {
                val intent = Intent(this@DetailProductActivity, DetailProductActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                intent.putExtra("CATEGORY_ID", product.categoryId)
                startActivity(intent)
            }
        })
        suggestRecyclerView.adapter = adapter
    }

    override fun backHome() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun setupBottomNavigation() {
        binding.bottomNavigation.findViewById<View>(R.id.detail_cart_plus).setOnClickListener {
            showAddToCartDialog(currentProduct)
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
        cartBadge = BadgeDrawable.create(this)
        cartBadge.isVisible = false
        cartBadge.backgroundColor = ContextCompat.getColor(this, R.color.badge_background_color)
        cartBadge.horizontalOffset = dpToPx(10)
        cartBadge.verticalOffset = dpToPx(2)
        val cartIcon = binding.detailCartItem
        BadgeUtils.attachBadgeDrawable(cartBadge, cartIcon, binding.detailCartItemContainer)
    }

    override fun navigateCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }

    override fun navigateHotline() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "hotline")
        }
        startActivity(intent)
        finish()
    }

    override fun navigateAllReviews() {
        val intent = Intent(this,AllReviewActivity::class.java).apply {
            putExtra("PRODUCT_ID",productId)
        }
        startActivity(intent)
        finish()
    }

    override fun showAddToCartDialog(product: Product) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottomsheet_add_product_to_cart_layout, null)
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

        val price = if (product.offerPercentage > 0) {
            product.getDiscountedPrice()
        } else {
            product.price
        }


        priceText.text = NumberFormatUtils.formatPrice(price)
        stockText.text = product.stockCount.toString()

        Glide.with(this)
            .load(product.coverImageUrl)
            .into(productImage)
        productImage.setOnClickListener {
            showFullscreenImage(product.coverImageUrl)
        }
        decreaseBtn.setOnClickListener {
            var quantity = quantityEdit.text.toString().toIntOrNull() ?: 1
            presenter.onDecreaseQuantity(quantity, product.stockCount)
        }
        increaseBtn.setOnClickListener {
            var quantity = quantityEdit.text.toString().toIntOrNull() ?: 1
            presenter.onIncreaseQuantity(quantity, product.stockCount)
        }
        quantityEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(e: Editable?) {
                val inputText = e.toString()
                val quantity = inputText.toIntOrNull() ?: 0
                if (quantity > product.stockCount) {
                    quantityEdit.setText(product.stockCount.toString())
                    quantityEdit.setSelection(product.stockCount.toString().length)
                }
                presenter.onQuantityChanged(
                    quantity.coerceAtMost(product.stockCount),
                    product.stockCount
                )
            }

        })

        addToCartBtn.setOnClickListener {
            var quantity = quantityEdit.text.toString().toIntOrNull() ?: 1
            presenter.onAddToCartClicked(product.id, quantity, price)
        }

        closeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun showReviews(reviews: List<Review>, users: Map<String, User>) {
        val layoutReviewProduct = binding.layoutReviewProduct

        if(reviews.isEmpty()){
            reviewBinding.root.visibility =View.GONE
        }
        else{
            reviewBinding.root.visibility =View.VISIBLE
            adapter.updateData(reviews,users)

            val averageRating = reviews.map { it.rating }.average().toFloat()
            layoutReviewProduct.ratingProductStar.rating = averageRating
            layoutReviewProduct.tvNumberStars.text = String.format("%.1f/5", averageRating)
            layoutReviewProduct.countReview.text = "(${reviews.size} đánh giá)"


            layoutReviewProduct.tvAllCountReview.text = "Xem Tất Cả (${reviews.size}) >"
        }
    }

    override fun showProductSoldCount(count: Int) {
        binding.tvSold.text=getString(R.string.sold_count_format,count)
    }

    override fun updateQuantity(quantity: Int) {
        bottomSheetDialog.findViewById<EditText>(R.id.quantity_edit)?.setText(quantity.toString())
    }

    override fun dismissAddToCartDialog() {
        bottomSheetDialog.dismiss()
    }

    override fun setAddToCartButtonEnabled(isEnabled: Boolean) {
        findViewById<Button>(R.id.btn_add_to_cart)?.isEnabled = isEnabled
        findViewById<Button>(R.id.btn_add_to_cart)?.alpha = 0.5f
    }

    override fun showQuantityExceededDialog(currentQuantity: Int) {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.error_limit_stock_count_add_cart, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        val messageTextView = dialogView.findViewById<TextView>(R.id.tv_message)
        messageTextView.text = getString(R.string.max_purchase_limit, currentQuantity)

        dialogView.findViewById<TextView>(R.id.tv_ok).setOnClickListener {
            dialog.dismiss()
            bottomSheetDialog.dismiss()
        }

        dialog.show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    //show full screenimage
    private fun showFullscreenImage(imageUrl: String) {
        val dialogs = Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialogs.setContentView(R.layout.fullscreen_image_layout)

        val fullscreenImage = dialogs.findViewById<ImageView>(R.id.fullscreen_image)
        val closeButton = dialogs.findViewById<ImageView>(R.id.close_button)

        Glide.with(this)
            .load(imageUrl)
            .into(fullscreenImage)

        closeButton.setOnClickListener {
            dialogs.dismiss()
        }

        fullscreenImage.setOnClickListener {
            dialogs.dismiss()
        }
        dialogs.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}