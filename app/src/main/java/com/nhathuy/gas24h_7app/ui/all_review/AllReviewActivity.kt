package com.nhathuy.gas24h_7app.ui.all_review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.ReviewAdapter
import com.nhathuy.gas24h_7app.data.model.Review
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityAllReviewBinding
import com.nhathuy.gas24h_7app.databinding.DialogReviewStarsBinding
import com.nhathuy.gas24h_7app.ui.cart.CartActivity
import javax.inject.Inject

class AllReviewActivity : AppCompatActivity(),AllReviewContract.View {

    private lateinit var binding:ActivityAllReviewBinding
    private lateinit var adapter: ReviewAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var productId:String? = null
    private var starCounts: Map<Int, Int> = emptyMap()
    @Inject
    lateinit var presenter:AllReviewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        productId = intent.getStringExtra("PRODUCT_ID") ?: ""

        setupRecyclerView()
        setupListeners()
        presenter.loadReviews(productId!!)
        presenter.loadCartItemCount()
    }

    private fun setupListeners() {
        binding.linearReviewHaveImageVideo.setOnClickListener {
            presenter.loadReviewsHaveVideoOrImage(productId!!)
        }
        binding.linearAllReview.setOnClickListener {
            presenter.loadReviews(productId!!)
        }
        binding.linearFilterStar.setOnClickListener {
            showDialogStar()
        }
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter()
        binding.recyclerView.adapter = adapter
    }

    override fun showLoading() {
        binding.loadingTextView.visibility= View.VISIBLE
        binding.contentScrollView.visibility= View.GONE
    }

    override fun hideLoading() {
        binding.loadingTextView.visibility=View.GONE
        binding.contentScrollView.visibility=View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this , message , Toast.LENGTH_SHORT).show()
    }

    override fun showReviews(reviews: List<Review>, users: Map<String, User>) {
        adapter.updateData(reviews,users)
        adapter.showAllReviews()
        val averageRating = reviews.map { it.rating }.average().toFloat()
        binding.ratingProductStar.rating = averageRating
        binding.tvNumberStars.text = String.format("%.1f/5", averageRating)
        binding.tvCountReview.text = "(${reviews.size})"
    }

    override fun showReviewsHaveVideoOrImage(reviews: List<Review>, users: Map<String, User>) {
        adapter.updateData(reviews,users)
        adapter.showAllReviews()
        binding.tvCountSizeImageVideo.text = "(${reviews.size})"
    }

    override fun showFilteredReviews(reviews: List<Review>, users: Map<String, User>) {
        adapter.updateData(reviews,users)
        adapter.showAllReviews()
    }

    override fun showDialogStar() {
        bottomSheetDialog = BottomSheetDialog(this)
        val binding = DialogReviewStarsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        // Update star counts here
        binding.textCount5Star.text = starCounts[5]?.toString() ?: "0"
        binding.textCount4Star.text = starCounts[4]?.toString() ?: "0"
        binding.textCount3Star.text = starCounts[3]?.toString() ?: "0"
        binding.textCount2Star.text = starCounts[2]?.toString() ?: "0"
        binding.textCount1Star.text = starCounts[1]?.toString() ?: "0"

        val checkBoxes = listOf(
            binding.checkbox5Star,
            binding.checkbox4Star,
            binding.checkbox3Star,
            binding.checkbox2Star,
            binding.checkbox1Star
        )

        checkBoxes.forEachIndexed { index, checkBox ->
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checkBoxes.forEachIndexed { otherIndex, otherCheckBox ->
                        if (otherIndex != index) {
                            otherCheckBox.isChecked = false
                        }
                    }
                }
            }
        }
        binding.btnApplyFilter.setOnClickListener {
            val selectedRating = when {
                binding.checkbox5Star.isChecked -> 5f
                binding.checkbox4Star.isChecked -> 4f
                binding.checkbox3Star.isChecked -> 3f
                binding.checkbox2Star.isChecked -> 2f
                binding.checkbox1Star.isChecked -> 1f
                else -> null
            }

            presenter.loadReviewsByRating(productId!!, selectedRating!!)
            bottomSheetDialog.dismiss()
        }
        binding.btnCancelFilter.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    override fun updateCartItemCount(count: Int) {
        val cartBadge = binding.buyBackCartItemContainer.findViewById<TextView>(R.id.cart_badge)
        if(count>0){
            cartBadge.visibility = View.VISIBLE
            cartBadge.text = if (count > 99) "99+" else count.toString()
        }
        else{
            cartBadge.visibility= View.GONE
        }
    }

    override fun updateReviewsCounts(totalCount: Int, imageVideoCount: Int) {
        binding.tvCountReview.text = "($totalCount)"
        binding.tvCountSizeImageVideo.text = "($imageVideoCount)"
    }

    override fun updateStartCount(starCounts: Map<Int, Int>) {
        this.starCounts = starCounts
        // If the dialog is showing, update it
        if (::bottomSheetDialog.isInitialized && bottomSheetDialog.isShowing) {
            showDialogStar()
        }
    }

    override fun navigateCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}