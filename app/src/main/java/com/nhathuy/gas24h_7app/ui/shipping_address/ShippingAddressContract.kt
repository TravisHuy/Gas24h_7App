package com.nhathuy.gas24h_7app.ui.shipping_address

import com.nhathuy.gas24h_7app.data.model.User

interface ShippingAddressContract {
    interface View {
        fun showMessage(message:String)
        fun showInformationUser(user:User)
//        fun setProvinces(provinces: List<String>)
//        fun setDistricts(districts: List<String>)
//        fun setWards(wards: List<String>)
        fun setAddressFields(province:String,district:String,ward:String,houseNumber:String)
        fun getUserFullName():String
        fun getUserPhone():String
        fun getProvince():String
        fun getDistrict():String
        fun getWard():String
        fun getHouseName():String
        fun onAddressSubmitted()
        fun navigateGoogleMap()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadUsers()
//        fun loadProvinces()
//        fun onProvinceSelected(provinceName: String)
//        fun onDistrictSelected(districtName: String)
        fun parseAndSetAddress(address:String)
        fun getCurrentAddress():String?
        fun onSubmitAddress(user:User)
    }
}