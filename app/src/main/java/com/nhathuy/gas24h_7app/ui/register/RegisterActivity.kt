package com.nhathuy.gas24h_7app.ui.register

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.District
import com.nhathuy.gas24h_7app.data.model.Province
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.model.Ward
import com.nhathuy.gas24h_7app.databinding.ActivityRegisterBinding
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    private lateinit var binding:ActivityRegisterBinding

    private lateinit var provinceAdapter:ArrayAdapter<String>
    private lateinit var districtAdapter:ArrayAdapter<String>
    private lateinit var wardAdapter: ArrayAdapter<String>

    private lateinit var currentUser: FirebaseUser

    @Inject
    lateinit var presenter: RegisterContract.Presenter
    @Inject
    lateinit var auth: FirebaseAuth

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

        setDropdownHeight(binding.wardAutoComplete, 4)
        setDropdownHeight(binding.provinceAutoComplete, 5)
        setDropdownHeight(binding.districtAutoComplete, 5)

        setupAdapters()
        setupListeners()
        setupTextWatchers()
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
        binding.tvRegLocation.setOnClickListener {
            requestLocationPermission()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }
    private fun setupTextWatchers() {
        binding.edRegFullName.addTextChangedListener(createTextWatcher { binding.tilFullName.error = null })
        binding.edRegAddress.addTextChangedListener(createTextWatcher { binding.tilAddress.error = null })
        binding.provinceAutoComplete.addTextChangedListener(createTextWatcher { binding.tilProvince.error = null })
        binding.districtAutoComplete.addTextChangedListener(createTextWatcher { binding.tilDistrict.error = null })
        binding.wardAutoComplete.addTextChangedListener(createTextWatcher { binding.tilWard.error = null })
    }

    private fun createTextWatcher(clearError: () -> Unit): TextWatcher {
        return object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearErrors()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
    }

    private fun registerUser() {
        currentUser=auth.currentUser!!
        if(currentUser!=null){
            val phoneNumber:String=currentUser.phoneNumber!!
            val user=User(
                uid=currentUser.uid,
                fullName = getFullName(),
                phoneNumber=phoneNumber,
                province = getSelectedProvince(),
                district = getSelectedDistrict(),
                ward= getSelectedWard(),
                address = getAddress(),
                referralCode = getReferralCode())
            if (presenter.validateUserInput(user)) {
                presenter.registerUser(user)
            }
        }

    }

    private fun setDropdownHeight(wardAutoComplete: AutoCompleteTextView, maxItems: Int) {
        wardAutoComplete.post {
            val itemHeight = resources.getDimensionPixelSize(R.dimen.max_dropdown_height) // Approximate item height
            wardAutoComplete.dropDownHeight = itemHeight * maxItems
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

    override fun showFullNameError(error: String) {
        binding.tilFullName.error=error
    }

    override fun showAddressError(error: String) {
        binding.tilAddress.error=error
    }

    override fun showProvinceError(error: String) {
        binding.tilProvince.error=error
    }

    override fun showDistrictError(error: String) {
        binding.tilDistrict.error=error
    }

    override fun showWardError(error: String) {
        binding.tilWard.error=error
    }

    override fun clearErrors() {
        binding.tilFullName.error=null
        binding.tilAddress.error=null
        binding.tilProvince.error=null
        binding.tilDistrict.error=null
        binding.tilWard.error=null
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}