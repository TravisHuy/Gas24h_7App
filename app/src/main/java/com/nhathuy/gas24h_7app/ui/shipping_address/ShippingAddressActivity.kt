package com.nhathuy.gas24h_7app.ui.shipping_address

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        if(address!=null){
            binding.addressTextView.text = address
            binding.addressTextView.visibility = View.VISIBLE
        }
        else{
            binding.addressTextView.visibility = View.GONE
        }

        setupListener()
        setupUI()
        presenter.loadUsers()
        presenter.loadProvinces()
    }
    private fun setupListener() {
        binding.linearChoosePlace.setOnClickListener {
            navigateGoogleMap()
        }
    }
    private fun setupUI() {
        setDropdownHeight(binding.wardAutoComplete, 4)
        setDropdownHeight(binding.provinceAutoComplete, 5)
        setDropdownHeight(binding.districtAutoComplete, 5)

        setupAdapters()
        setupListeners()
//        setupTextWatchers()
    }
    private fun setDropdownHeight(wardAutoComplete: AutoCompleteTextView, maxItems: Int) {
        wardAutoComplete.post {
            val itemHeight = resources.getDimensionPixelSize(R.dimen.max_dropdown_height) // Approximate item height
            wardAutoComplete.dropDownHeight = itemHeight * maxItems
        }
    }

    private fun setupAdapters() {
        // Initialize adapters
        provinceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf())
        districtAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        wardAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())

        // Set adapters to AutoCompleteTextViews
        binding.provinceAutoComplete.setAdapter(provinceAdapter)
        binding.districtAutoComplete.setAdapter(districtAdapter)
        binding.wardAutoComplete.setAdapter(wardAdapter)
    }

    private fun setupListeners() {
        binding.provinceAutoComplete.setOnItemClickListener { _, _, position, _ ->
            clearDistrictAndWard()
            val selectedProvince = binding.provinceAutoComplete.text.toString()
            presenter.onProvinceSelected(selectedProvince)
        }

        binding.districtAutoComplete.setOnItemClickListener { _, _, position, _ ->
            clearWard()
            val selectedDistrict = binding.districtAutoComplete.text.toString()
            presenter.onDistrictSelected(selectedDistrict)
        }
    }

    private fun clearDistrictAndWard() {
        binding.districtAutoComplete.setText("", false)
        binding.wardAutoComplete.setText("", false)
        districtAdapter.clear()
        wardAdapter.clear()
        districtAdapter.notifyDataSetChanged()
        wardAdapter.notifyDataSetChanged()
    }
    private fun clearWard() {
        binding.wardAutoComplete.setText("",false)
        wardAdapter.clear()
        wardAdapter.notifyDataSetChanged()
    }
    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showInformationUser(user: User) {
        binding.edRegFullName.setText(user.fullName)
        binding.edShippingAddressPhone.setText(user.phoneNumber)
        binding.provinceAutoComplete.setText(user.province)
        binding.districtAutoComplete.setText(user.district)
        binding.wardAutoComplete.setText(user.ward)
    }

    override fun setProvinces(provinces: List<String>) {
        provinceAdapter.clear()
        provinceAdapter.addAll(provinces)
        provinceAdapter.notifyDataSetChanged()
    }

    override fun setDistricts(districts: List<String>) {
        clearWard()
        districtAdapter.clear()
        districtAdapter.addAll(districts)
        districtAdapter.notifyDataSetChanged()
    }

    override fun setWards(wards: List<String>) {
        wardAdapter.clear()
        wardAdapter.addAll(wards)
        wardAdapter.notifyDataSetChanged()
    }

    override fun navigateGoogleMap() {
        startActivity(Intent(this,GoogleMapActivity::class.java))
    }

}