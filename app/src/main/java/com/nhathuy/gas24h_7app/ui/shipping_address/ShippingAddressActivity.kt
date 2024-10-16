package com.nhathuy.gas24h_7app.ui.shipping_address

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.ActivityShippingAddressBinding
import com.nhathuy.gas24h_7app.ui.google_map.GoogleMapActivity
import com.nhathuy.gas24h_7app.util.Constants.GOOGLE_MAP_REQUEST_CODE
import javax.inject.Inject

class ShippingAddressActivity : AppCompatActivity(), ShippingAddressContract.View {

    private lateinit var binding:ActivityShippingAddressBinding

    private lateinit var provinceAdapter:ArrayAdapter<String>
    private lateinit var districtAdapter:ArrayAdapter<String>
    private lateinit var wardAdapter: ArrayAdapter<String>


    @Inject
    lateinit var presenter: ShippingAddressPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
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

        setupListeners()
        presenter.loadUsers()
    }
    private fun setupListeners() {
//        binding.provinceAutoComplete.setOnItemClickListener { _, _, position, _ ->
//            val selectedProvince = provinceAdapter.getItem(position)
//            presenter.onProvinceSelected(selectedProvince  ?: "")
//        }
//
//        // District selection
//        binding.districtAutoComplete.setOnItemClickListener { _, _, position, _ ->
//            val selectedDistrict = districtAdapter.getItem(position)
//            Log.d("ShippingAddress", "Selected district: $selectedDistrict")
//            presenter.onDistrictSelected(selectedDistrict ?: "")
//        }
//
//        // Ward selection
//        binding.wardAutoComplete.setOnItemClickListener { _, _, position, _ ->
//            val selectedWard = wardAdapter.getItem(position)
//            Log.d("ShippingAddress", "Selected ward: $selectedWard")
//        }
        binding.linearChoosePlace.setOnClickListener {
            navigateGoogleMap()
        }
        binding.btnConfirm.setOnClickListener {
            val user = User(
                fullName = getUserFullName(),
                phoneNumber = getUserPhone(),
                province = getProvince(),
                district = getDistrict(),
                ward = getWard(),
                houseNumber = getHouseName(),
                address = "${getHouseName()} ,${getWard()},${getDistrict()},${getProvince()}"
            )
            presenter.onSubmitAddress(user)
        }
    }

//    private fun clearDistrictAndWard() {
//        binding.districtAutoComplete.setText("", false)
//        binding.wardAutoComplete.setText("", false)
//        districtAdapter.clear()
//        wardAdapter.clear()
//        districtAdapter.notifyDataSetChanged()
//        wardAdapter.notifyDataSetChanged()
//    }
//    private fun clearWard() {
//        binding.wardAutoComplete.setText("",false)
//        wardAdapter.clear()
//        wardAdapter.notifyDataSetChanged()
//    }
    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showInformationUser(user: User) {
        binding.edRegFullName.setText(user.fullName)
        binding.edShippingAddressPhone.setText(user.phoneNumber)
        binding.edProvince.setText(user.province)
        binding.edDistrict.setText(user.district)
        binding.edWard.setText(user.ward)
        binding.edShippingAddressHouseNumber.setText(user.houseNumber)
    }

//    override fun setProvinces(provinces: List<String>) {
//        Log.d("ShippingAddress", "Setting provinces: ${provinces.size}")
//        provinceAdapter.clear()
//        provinceAdapter.addAll(provinces)
//        provinceAdapter.notifyDataSetChanged()
//        binding.provinceAutoComplete.setAdapter(provinceAdapter)
//    }
//
//    override fun setDistricts(districts: List<String>) {
//        Log.d("ShippingAddress", "Setting districts: ${districts.size}")
//        clearWard()
//        districtAdapter.clear()
//        districtAdapter.addAll(districts)
//        districtAdapter.notifyDataSetChanged()
//        binding.districtAutoComplete.setAdapter(districtAdapter)
//    }
//
//    override fun setWards(wards: List<String>) {
//        Log.d("ShippingAddress", "Setting wards: ${wards.size}")
//        wardAdapter.clear()
//        wardAdapter.addAll(wards)
//        wardAdapter.notifyDataSetChanged()
//        binding.wardAutoComplete.setAdapter(wardAdapter)
//    }

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

    override fun getUserFullName(): String = binding.edRegFullName.text.toString()

    override fun getUserPhone(): String  = binding.edShippingAddressPhone.text.toString()

    override fun getProvince(): String = binding.edProvince.text.toString()

    override fun getDistrict(): String = binding.edDistrict.text.toString()

    override fun getWard(): String = binding.edWard.text.toString()

    override fun getHouseName(): String = binding.edShippingAddressHouseNumber.text.toString()

    override fun onAddressSubmitted() {
        Toast.makeText(this, "Địa chỉ đã được cập nhật", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun navigateGoogleMap() {
        val intent = Intent(this, GoogleMapActivity::class.java)
        intent.putExtra("currentAddress",presenter.getCurrentAddress())
        startActivityForResult(intent, GOOGLE_MAP_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_MAP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val address = data?.getStringExtra("address")
            if (address != null) {
                presenter.parseAndSetAddress(address)
            }
        }
    }

}