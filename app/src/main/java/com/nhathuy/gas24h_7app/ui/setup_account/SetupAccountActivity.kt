package com.nhathuy.gas24h_7app.ui.setup_account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivitySetupAccountBinding
import com.nhathuy.gas24h_7app.fragment.profile.ProfileFragment
import com.nhathuy.gas24h_7app.ui.google_map.GoogleMapActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import com.nhathuy.gas24h_7app.util.Constants
import javax.inject.Inject

class SetupAccountActivity : AppCompatActivity(),SetupAccountContract.View {

    private lateinit var binding:ActivitySetupAccountBinding
    @Inject
    lateinit var presenter: SetupAccountPresenter
    private var currentImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val address = intent.getStringExtra("address")
        if(address !=null){
            presenter.parseAndSetAddress(address)
            binding.addressTextView.text
        }

        if(address!=null){
            binding.addressTextView.text = address
            binding.addressTextView.visibility = View.VISIBLE
        }
        else{
            binding.addressTextView.visibility = View.GONE
        }

        presenter.loadUserInfo()
        setupListeners()
    }

    private fun setupListeners() {
        binding.profileImage.setOnClickListener{
            openImagePicker()
        }
        binding.btnConfirm.setOnClickListener {
            if (validateInput()) {
                val user = User(
                    fullName = getUserFullName(),
                    imageUser =currentImageUrl,
                    province = getProvince(),
                    district = getDistrict(),
                    ward = getWard(),
                    houseNumber = getHouseName(),
                    address = "${getHouseName()}, ${getWard()}, ${getDistrict()}, ${getProvince()}"
                )
                presenter.onSubmit(user)
            }
        }
        binding.linearChoosePlace.setOnClickListener {
            navigateGoogleMap()
        }
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
            Constants.IMAGE_PICK_ACCOUNT
        )
    }


    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showUpdateProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_person_circle)
            .into(binding.profileImage)
        currentImageUrl = imageUrl
    }

    override fun showInfoUser(user: User) {
        Glide.with(this)
            .load(user.imageUser)
            .placeholder(R.drawable.ic_person_circle)
            .into(binding.profileImage)
        currentImageUrl = user.imageUser
        binding.fullName.setText(user.fullName)
        binding.edProvince.setText(user.province)
        binding.edDistrict.setText(user.district)
        binding.edWard.setText(user.ward)
        binding.edShippingAddressHouseNumber.setText(user.houseNumber)
    }

    override fun setAddressFields(
        province: String,
        district: String,
        ward: String,
        houseNumber: String
    ) {
        binding.edProvince.setText(province)
        binding.edDistrict.setText(district)
        binding.edWard.setText(ward)
        binding.edShippingAddressHouseNumber.setText(houseNumber)
    }

    override fun getUserFullName(): String = binding.fullName.text.toString()

    override fun getProvince(): String = binding.edProvince.text.toString()

    override fun getDistrict(): String = binding.edDistrict.text.toString()

    override fun getWard(): String = binding.edWard.text.toString()

    override fun getHouseName(): String = binding.edShippingAddressHouseNumber.text.toString()

    override fun onSubmitted() {
        Toast.makeText(this, "cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun navigateGoogleMap() {
        val intent = Intent(this, GoogleMapActivity::class.java)
        intent.putExtra("currentAddress",presenter.getCurrentAddress())
        startActivityForResult(intent, Constants.GOOGLE_MAP_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.GOOGLE_MAP_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val address = data?.getStringExtra("address")
                    address?.let { presenter.parseAndSetAddress(it) }
                }
            }
            Constants.IMAGE_PICK_ACCOUNT -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        presenter.updateProfileImage(uri)
                    }
                }
            }
        }
    }

    override fun navigateProfile() {
        onBackPressed()
    }
    private fun validateInput(): Boolean {
        var isValid = true

        // Kiểm tra tên đầy đủ
        if (getUserFullName().trim().isEmpty()) {
            binding.fullName.error = "Vui lòng nhập tên đầy đủ"
            isValid = false
        }

        // Kiểm tra địa chỉ
        if (getProvince().trim().isEmpty()) {
            binding.edProvince.error = "Vui lòng nhập tỉnh/thành phố"
            isValid = false
        }

        if (getDistrict().trim().isEmpty()) {
            binding.edDistrict.error = "Vui lòng nhập quận/huyện"
            isValid = false
        }

        if (getWard().trim().isEmpty()) {
            binding.edWard.error = "Vui lòng nhập phường/xã"
            isValid = false
        }

        if (getHouseName().trim().isEmpty()) {
            binding.edShippingAddressHouseNumber.error = "Vui lòng nhập số nhà"
            isValid = false
        }

        return isValid
    }
}