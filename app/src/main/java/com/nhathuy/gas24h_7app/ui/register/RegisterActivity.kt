package com.nhathuy.gas24h_7app.ui.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.District
import com.nhathuy.gas24h_7app.data.model.Province
import com.nhathuy.gas24h_7app.data.model.Ward
import com.nhathuy.gas24h_7app.databinding.ActivityRegisterBinding
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding:ActivityRegisterBinding

    private lateinit var provinceAdapter:ArrayAdapter<String>
    private lateinit var districtAdapter:ArrayAdapter<String>
    private lateinit var wardAdapter: ArrayAdapter<String>


    @Inject
    lateinit var presenter: RegisterContract.Presenter


    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupUI()
        presenter.loadProvinces()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.fetchCurrentAddress()
            } else {
                showError("Location permission denied")
            }
        }
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            presenter.fetchCurrentAddress()
        }
    }
    private fun setupUI() {

        // Initialize adapters
        provinceAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        districtAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        wardAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())

        // Set adapters to AutoCompleteTextViews
        binding.provinceAutoComplete.setAdapter(provinceAdapter)
        binding.districtAutoComplete.setAdapter(districtAdapter)
        binding.wardAutoComplete.setAdapter(wardAdapter)


        binding.provinceAutoComplete.setOnItemClickListener { _, _, _, _ ->
            presenter.onProvinceSelected(binding.provinceAutoComplete.text.toString())
            clearDistrictAndWard()
        }

        binding.districtAutoComplete.setOnItemClickListener { _, _, _, _ ->
            presenter.onDistrictSelected(binding.districtAutoComplete.text.toString())
            clearWard()
        }
//        binding.wardAutoComplete.setOnItemClickListener { _, _, _, _ ->
////            presenter.onWardSelected(binding.wardAutoComplete.text.toString())
//        }
        binding.tvRegLocation.setOnClickListener {
            requestLocationPermission()
        }
        binding.btnRegister.setOnClickListener {

        }

    }



    private fun clearDistrictAndWard() {
        binding.districtAutoComplete.text.clear()
        binding.wardAutoComplete.text.clear()
        districtAdapter.clear()
        wardAdapter.clear()
    }
    private fun clearWard() {
        binding.wardAutoComplete.text.clear()
        wardAdapter.clear()
    }
    override fun showLoading() {
        binding.progressBar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility=View.GONE
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun setProvinces(provinces: List<String>) {
        provinceAdapter.clear()
        provinceAdapter.addAll(provinces)
        provinceAdapter.notifyDataSetChanged()
    }

    override fun setDistricts(districts: List<String>) {
        districtAdapter.clear()
        districtAdapter.addAll(districts)
        districtAdapter.notifyDataSetChanged()
    }

    override fun setWards(wards: List<String>) {
        wardAdapter.clear()
        wardAdapter.addAll(wards)
        wardAdapter.notifyDataSetChanged()
    }


    override fun navigateMain() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun getFullName(): String = binding.edRegFullName.text.toString()
    override fun getAddress(): String = binding.edRegAddress.text.toString()
    override fun getReferralCode(): String =binding.edRegReferalcode.text.toString()

    override fun getSelectedProvince(): String = binding.provinceAutoComplete.text.toString()
    override fun getSelectedDistrict(): String = binding.districtAutoComplete.text.toString()
    override fun getSelectedWard(): String = binding.wardAutoComplete.text.toString()
    override fun setAddress(address: String) {
        binding.edRegAddress.setText(address)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}