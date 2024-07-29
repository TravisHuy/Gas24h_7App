package com.nhathuy.gas24h_7app.ui.register

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.api.LocationApiService
import com.nhathuy.gas24h_7app.data.model.District
import com.nhathuy.gas24h_7app.data.model.Province
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.model.Ward
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

import javax.inject.Inject


class RegisterPresenter @Inject constructor(private val locationApiService: LocationApiService,
                                            private val userRepository: UserRepository,
                                            private val coroutineScope: CoroutineScope,
                                            private val context: Context
):RegisterContract.Presenter {

    private var view:RegisterContract.View?=null
    private lateinit var provinces: MutableList<Province>
    private var districts: MutableMap<String, List<District>> = mutableMapOf()
    private var wards: MutableMap<String, List<Ward>> = mutableMapOf()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun attachView(view: RegisterContract.View) {
        this.view=view
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(context)
    }

    override fun detachView() {
        view=null
    }

    override fun registerUser(user: User) {
        if(!validateUserInput(user)){
            return
        }
        coroutineScope.launch {
            view?.showLoading()
            try {
                val result= userRepository.registerUser(user)
                withContext(Dispatchers.Main){
                    if(result.isSuccess){
                        view?.showSuccess("Registration successful")
                        view?.navigateMain()
                    }
                    else{
                        view?.showError("Registration failed: ${result.exceptionOrNull()?.message}")
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    view?.showError("Registration failed: ${e.message}")
                }
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun validateUserInput(user: User): Boolean {
        var isValid=true
        view?.clearErrors()
        when {
            user.fullName.isBlank() -> {
                view?.showFullNameError("Please enter your full name")
                isValid=false
            }
            user.province.isBlank() -> {
                view?.showProvinceError("Please select a province")
                isValid=false
            }
            user.district.isBlank() -> {
                view?.showDistrictError("Please select a district")
                isValid=false
            }
            user.ward.isBlank() -> {
                view?.showWardError("Please select a ward")
                isValid=false
            }
            user.address.isBlank() -> {
                view?.showAddressError("Please enter your address")
                isValid=false
            }
        }
        return isValid
    }

    override fun loadProvinces() {
        coroutineScope.launch {
            try {
                val response= withContext(Dispatchers.IO){
                    locationApiService.getProvides()
                }
                provinces=response.data.toMutableList()
                view?.setProvinces(provinces.map { it.full_name })
            }
            catch (e:Exception){
                view?.showError("Error fetching provinces: ${e.message}")
            }
        }
    }

    override fun onProvinceSelected(provinceName: String) {
        val province =provinces.find { it.full_name==provinceName }?:return

        coroutineScope.launch {
                try {
                    view?.showLoading()
                    val response = withContext(Dispatchers.IO) {
                        locationApiService.getDistricts(province.id)
                    }
//                districts= mapOf(province.id to response.data)
                    districts[province.id] = response.data
                    view?.setDistricts(response.data.map { it.full_name })
                    view?.hideLoading()
                } catch (e: Exception) {
                    view?.hideLoading()
                    view?.showError("Error fetching districts: ${e.message}")
                }
            }
    }

    override fun onDistrictSelected(districtName: String) {
        val selectedProvince = provinces.find { province ->
            districts[province.id]?.any { it.full_name == districtName } == true
        } ?: return

        val district = districts[selectedProvince.id]?.find { it.full_name == districtName } ?: return


        coroutineScope.launch {
                try {
                    view?.showLoading()
                    val response = withContext(Dispatchers.IO) {
                        locationApiService.getWards(district.id)
                    }
//                wards= mapOf(district.id to response.data)
                    wards[district.id] = response.data
                    view?.setWards(response.data.map { it.full_name })
                    view?.hideLoading()
                } catch (e: Exception) {
                    view?.hideLoading()
                    view?.showError("Error fetching districts: ${e.message}")
                }
        }
    }
    @SuppressLint("MissingPermission")
    override fun fetchCurrentAddress() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    getAddressFromLocation(location)
                } else {
                    view?.showError("Unable to get current location")
                }
            }
            .addOnFailureListener { e ->
                view?.showError("Error getting location: ${e.message}")
            }
    }

    private fun getAddressFromLocation(location: Location) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                withContext(Dispatchers.Main) {
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val addressText = address.getAddressLine(0)
                        view?.setAddress(addressText)
                    } else {
                        view?.showError("Unable to get address from location")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Error getting address: ${e.message}")
                }
            }
        }
    }

}