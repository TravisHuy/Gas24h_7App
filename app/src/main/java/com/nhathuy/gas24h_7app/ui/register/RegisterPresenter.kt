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
import com.nhathuy.gas24h_7app.data.api.LocationRetrofit
import com.nhathuy.gas24h_7app.data.api.VietNamAddressApiService
import com.nhathuy.gas24h_7app.data.api.VietNamAddressRetrofit
import com.nhathuy.gas24h_7app.data.model.District
import com.nhathuy.gas24h_7app.data.model.Districts
import com.nhathuy.gas24h_7app.data.model.Province
import com.nhathuy.gas24h_7app.data.model.Provinces
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.model.Ward
import com.nhathuy.gas24h_7app.data.model.Wards
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

import javax.inject.Inject


class RegisterPresenter @Inject constructor(@VietNamAddressRetrofit private val vietNamAddressApiService: VietNamAddressApiService,
                                            private val userRepository: UserRepository,
                                            private val coroutineScope: CoroutineScope,
                                            private val context: Context
):RegisterContract.Presenter {

    private var view:RegisterContract.View?=null
    private lateinit var provinces: MutableList<Provinces>
    private var districts: MutableMap<String, List<Districts>> = mutableMapOf()
    private var wards: MutableMap<String, List<Wards>> = mutableMapOf()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isUsingCurrentLocation = false
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
                if(!isUsingCurrentLocation){
                    user.houseNumber = user.address
                    user.address = "${user.houseNumber}, ${user.ward}, ${user.district},${user.province}"
                }
                else{
                    user.houseNumber = ""
                }
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
                view?.showAddressError("Vui lòng nhập số nhà, tên đường hoặc sử dụng vị trí hiện tại")
                isValid=false
            }
        }
        return isValid
    }
    override fun loadProvinces() {
        coroutineScope.launch {
            try {
                val response= withContext(Dispatchers.IO){
                    vietNamAddressApiService.getAllProvinces()
                }
                if (response.isSuccessful) {
                    provinces = response.body()?.toMutableList() ?: mutableListOf()
                    view?.setProvinces(provinces.map { it.Name})
                } else {
                    view?.showError("Failed to fetch provinces: ${response.message()}")
                }
            }
            catch (e:Exception){
                view?.showError("Error fetching provinces: ${e.message}")
            }
        }
    }

    override fun onProvinceSelected(provinceName: String) {
        val province =provinces.find { it.Name==provinceName }?:return

        coroutineScope.launch {
            try {
                view?.showLoading()
                val response = withContext(Dispatchers.IO) {
                    vietNamAddressApiService.getDistrictsByProvinceId(province.Id)
                }
//                districts= mapOf(province.id to response.data)
                if (response.isSuccessful) {
                    districts[province.Id] = response.body() ?: listOf()
                    view?.setDistricts(districts[province.Id]?.map { it.Name } ?: listOf())
                } else {
                    view?.showError("Failed to fetch districts: ${response.message()}")
                }
                view?.hideLoading()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError("Error fetching districts: ${e.message}")
            }
        }
    }

    override fun onDistrictSelected(districtName: String) {
        val selectedProvince = provinces.find { province ->
            districts[province.Id]?.any { it.Name == districtName } == true
        } ?: return

        val district = districts[selectedProvince.Id]?.find { it.Name == districtName } ?: return


        coroutineScope.launch {
            try {
                view?.showLoading()
                val response = withContext(Dispatchers.IO) {
                   vietNamAddressApiService.getWardsByDistrictId(district.Id)
                }
//                wards= mapOf(district.id to response.data)
                if (response.isSuccessful) {
                    wards[district.Id] = response.body() ?: listOf()
                    view?.setWards(wards[district.Id]?.map { it.Name } ?: listOf())
                } else {
                    view?.showError("Failed to fetch wards: ${response.message()}")
                }
                view?.hideLoading()
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError("Error fetching districts: ${e.message}")
            }
        }
    }
//    override fun loadProvinces() {
//        coroutineScope.launch {
//            try {
//                val response= withContext(Dispatchers.IO){
//                    locationApiService.getProvides()
//                }
//                provinces=response.data.toMutableList()
//                view?.setProvinces(provinces.map { it.full_name })
//            }
//            catch (e:Exception){
//                view?.showError("Error fetching provinces: ${e.message}")
//            }
//        }
//    }
//
//    override fun onProvinceSelected(provinceName: String) {
//        val province =provinces.find { it.full_name==provinceName }?:return
//
//        coroutineScope.launch {
//                try {
//                    view?.showLoading()
//                    val response = withContext(Dispatchers.IO) {
//                        locationApiService.getDistricts(province.id)
//                    }
////                districts= mapOf(province.id to response.data)
//                    districts[province.id] = response.data
//                    view?.setDistricts(response.data.map { it.full_name })
//                    view?.hideLoading()
//                } catch (e: Exception) {
//                    view?.hideLoading()
//                    view?.showError("Error fetching districts: ${e.message}")
//                }
//            }
//    }
//
//    override fun onDistrictSelected(districtName: String) {
//        val selectedProvince = provinces.find { province ->
//            districts[province.id]?.any { it.full_name == districtName } == true
//        } ?: return
//
//        val district = districts[selectedProvince.id]?.find { it.full_name == districtName } ?: return
//
//
//        coroutineScope.launch {
//                try {
//                    view?.showLoading()
//                    val response = withContext(Dispatchers.IO) {
//                        locationApiService.getWards(district.id)
//                    }
////                wards= mapOf(district.id to response.data)
//                    wards[district.id] = response.data
//                    view?.setWards(response.data.map { it.full_name })
//                    view?.hideLoading()
//                } catch (e: Exception) {
//                    view?.hideLoading()
//                    view?.showError("Error fetching districts: ${e.message}")
//                }
//        }
//    }
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