package com.nhathuy.gas24h_7app.ui.register

import com.nhathuy.gas24h_7app.data.model.District
import com.nhathuy.gas24h_7app.data.model.Province
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.model.Ward
import com.nhathuy.gas24h_7app.ui.login.LoginContract

interface RegisterContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showSuccess(message: String)
        fun showError(message: String)
        fun setProvinces(provinces: List<String>)
        fun setDistricts(districts: List<String>)
        fun setWards(wards: List<String>)
        fun navigateMain()
        fun getFullName(): String
        fun getAddress(): String
        fun getReferralCode(): String
        fun getSelectedProvince(): String
        fun getSelectedDistrict(): String
        fun getSelectedWard(): String
        fun setAddress(address:String)
    }
    interface Presenter{
        fun attachView(view: RegisterContract.View)
        fun detachView()
        fun registerUser(user: User)
        fun loadProvinces()
        fun onProvinceSelected(provinceName: String)
        fun onDistrictSelected(districtName: String)
        fun fetchCurrentAddress()
    }
}