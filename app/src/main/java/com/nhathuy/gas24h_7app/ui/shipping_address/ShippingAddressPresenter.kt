package com.nhathuy.gas24h_7app.ui.shipping_address

import com.nhathuy.gas24h_7app.data.api.LocationApiService
import com.nhathuy.gas24h_7app.data.model.District
import com.nhathuy.gas24h_7app.data.model.Province
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.model.Ward
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShippingAddressPresenter @Inject constructor(private val userRepository: UserRepository,
                                                   private val locationApiService: LocationApiService
):ShippingAddressContract.Presenter {

    private var view:ShippingAddressContract.View? = null

    private lateinit var provinces: MutableList<Province>
    private var districts: MutableMap<String, List<District>> = mutableMapOf()
    private var wards: MutableMap<String, List<Ward>> = mutableMapOf()

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private var currentAddress:String? = null
    override fun attachView(view: ShippingAddressContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadUsers() {
        coroutineScope.launch {
            try {
                val result = userRepository.getUser(userRepository.getCurrentUserId()!!)
                result.fold(
                    onSuccess = { user ->
                        view?.showInformationUser(user)
                        currentAddress = user.address
                    },
                    onFailure = {e ->
                        view?.showMessage("Failed load user ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Failed load user ${e.message}")
            }
        }
    }

//    override fun loadProvinces() {
//        coroutineScope.launch {
//            try {
//                val response  = withContext(Dispatchers.IO){
//                    locationApiService.getProvides()
//                }
//                provinces  = response.data.toMutableList()
//                view?.setProvinces(provinces.map { it.full_name })
//            }
//            catch (e:Exception){
//                view?.showMessage("Error fetching provinces: ${e.message}")
//            }
//        }
//    }
//
//    override fun onProvinceSelected(provinceName: String) {
//        val province = provinces.find { it.full_name == provinceName }?: return
//
//        coroutineScope.launch {
//            try {
//                val response  = withContext(Dispatchers.IO){
//                    locationApiService.getDistricts(province.id)
//                }
//                districts[province.id] = response.data
//                view?.setDistricts(response.data.map { it.full_name })
//            }
//            catch (e:Exception){
//
//            }
//        }
//    }
//
//    override fun onDistrictSelected(districtName: String) {
//        val selectedProvince = provinces.find { province ->
//            districts[province.id]?.any { it.full_name == districtName } == true
//        } ?: return
//
//        val district = districts[selectedProvince.id]?.find { it.full_name == districtName } ?: return
//
//        coroutineScope.launch {
//            try {
//                val response = withContext(Dispatchers.IO) {
//                    locationApiService.getWards(district.id)
//                }
//                wards[district.id] = response.data
//                view?.setWards(response.data.map { it.full_name })
//            } catch (e: Exception) {
//                view?.showMessage("Error fetching districts: ${e.message}")
//            }
//        }
//    }

    override fun parseAndSetAddress(address: String) {
        val addressParts = address.split(", ")
        var province = ""
        var district = ""
        var ward = ""
        var houseNumber = ""

        when{
            addressParts.size >=4 ->{
                province = addressParts[addressParts.size-2]
                district = addressParts[addressParts.size-3]
                ward = addressParts[addressParts.size-4]
                houseNumber = addressParts.subList(0,addressParts.size-4).joinToString(", ")
            }
            addressParts.size==3 ->{
                province = addressParts[2]
                district = addressParts[1]
                ward = addressParts[0]
            }
            addressParts.size==2 ->{
                province = addressParts[1]
                district = addressParts[0]
            }
            addressParts.size==1 ->{
                province = addressParts[0]
            }
        }
        view?.setAddressFields(province, district, ward, houseNumber)

//        // Load districts and wards based on the parsed address
//        if (province.isNotEmpty()) {
//            onProvinceSelected(province)
//        }
//        if (district.isNotEmpty()) {
//            onDistrictSelected(district)
//        }

    }

    override fun getCurrentAddress(): String? {
        return currentAddress
    }

    override fun onSubmitAddress(user: User) {
        coroutineScope.launch {
            try {
                val result = userRepository.updateUser(user)
                result.fold(
                    onSuccess = {
                        view?.onAddressSubmitted()
                    },
                    onFailure = {
                            e ->
                        view?.showMessage("Cập nhật địa chỉ thất bại: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }
}