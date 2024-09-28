package com.nhathuy.gas24h_7app.fragment.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.AllVoucherAdapter
import com.nhathuy.gas24h_7app.adapter.BuyBackAdapter
import com.nhathuy.gas24h_7app.adapter.BuyBackItemAdapter
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.FragmentProfileBinding
import com.nhathuy.gas24h_7app.ui.all_voucher.AllVoucherActivity
import com.nhathuy.gas24h_7app.ui.buy_back.BuyBackActivity
import com.nhathuy.gas24h_7app.ui.cart.CartActivity
import com.nhathuy.gas24h_7app.ui.detail_product.DetailProductActivity
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import com.nhathuy.gas24h_7app.ui.purchased_order.PurchasedOrderActivity
import javax.inject.Inject


class ProfileFragment : Fragment(R.layout.fragment_profile),ProfileContract.View,BuyBackAdapter.BuyBackClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BuyBackItemAdapter

    @Inject
    lateinit var presenter: ProfilePresenter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as Gas24h_7Application).getGasComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentProfileBinding.inflate(inflater,container,false)

        presenter.attachView(this)

        setupListeners()
        setupRecyclerview()

        presenter.loadOrders("DELIVERED")
        presenter.loadCartItemCount()
        presenter.loadOrderCount()
        return binding.root
    }

    private fun setupRecyclerview() {
        adapter = BuyBackItemAdapter(listener = this)
        binding.productPurchaseRec.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.productPurchaseRec.adapter = adapter
    }

    private fun setupListeners() {
        binding.linearWaitingConfirm.setOnClickListener {
            navigatePurchaseOrder()
        }

        binding.profileImage.setOnClickListener{
            openImagePicker()
        }

        binding.btnLogout.setOnClickListener {
            showDialogLogout()
        }
        binding.linearWatchingProduct.setOnClickListener {
            navigateBuyBack()
        }
        binding.btnLogin.setOnClickListener {
            navigateLogin()
        }
        binding.btnCart.setOnClickListener {
            navigateCart()
        }
        binding.linearAllVoucher.setOnClickListener {
            navigateAllVouchers()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                presenter.updateProfileImage(uri)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun navigatePurchaseOrder() {
        startActivity(Intent(requireContext(),PurchasedOrderActivity::class.java))
    }

    override fun showUpdateProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_person_circle)
            .into(binding.profileImage)
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }

    override fun showLoading(isLoading: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }

    override fun showUserInfo(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            binding.linearLogin.visibility = View.GONE
            binding.linearUserInformation.visibility = View.VISIBLE
        } else {
            binding.linearLogin.visibility = View.VISIBLE
            binding.linearUserInformation.visibility = View.GONE
            // Reset profile image and name
            binding.profileImage.setImageResource(R.drawable.ic_person_circle)
            binding.tvUserName.text = ""
        }
    }

    override fun updateProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_person_circle)
            .into(binding.profileImage)
    }

    override fun updateUserName(name: String) {
        binding.tvUserName.text= name
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

    override fun updateOrderCount(
        processingCount: Int,
        pendingCount: Int,
        shippedCount: Int,
        deliveredCount: Int
    ) {
        // Update pending orders count
        binding.tvWaitingConfirmCount.text = pendingCount.toString()
        binding.tvWaitingConfirmCount.visibility = if (pendingCount > 0) View.VISIBLE else View.GONE

        // Update processing orders count
        binding.tvHasReceivedCount.text = processingCount.toString()
        binding.tvHasReceivedCount.visibility = if (processingCount > 0) View.VISIBLE else View.GONE

        // Update shipped orders count
        binding.tvWaitingDeliveryCount.text = shippedCount.toString()
        binding.tvWaitingDeliveryCount.visibility = if (shippedCount > 0) View.VISIBLE else View.GONE

        // Update delivered orders count
        binding.tvReviewCount.text = deliveredCount.toString()
        binding.tvReviewCount.visibility = if (deliveredCount > 0) View.VISIBLE else View.GONE
    }


    override fun showDialogLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes"){
                dialog,_ ->
                logout()
                dialog.dismiss()
            }.setNegativeButton("No"){ dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    override fun showOrders(orders: List<Order>, products: Map<String, Product>) {
        adapter.updateOrderProducts(orders,products)


    }

    override fun navigateBuyBack() {
        startActivity(Intent(requireContext(),BuyBackActivity::class.java))
    }

    override fun navigateCart() {
        startActivity(Intent(requireContext(),CartActivity::class.java))
    }

    override fun navigateLogin() {
        startActivity(Intent(requireContext(),LoginActivity::class.java))
    }

    override fun navigateAllVouchers() {
        startActivity(Intent(requireContext(),AllVoucherActivity::class.java))
    }

    private fun logout() {
        presenter.logout()
        startActivity(Intent(requireContext(),MainActivity::class.java))
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onProductClick(productId: String) {
        val intent = Intent(requireContext(), DetailProductActivity::class.java).apply {
            putExtra("PRODUCT_ID", productId)
        }
        startActivity(intent)
    }
}