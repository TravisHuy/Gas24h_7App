package com.nhathuy.gas24h_7app.ui.shipping_address

import com.nhathuy.gas24h_7app.data.model.User

interface ShippingAddressContract {
    interface View {
        fun showMessage(message:String)
        fun showInformationUser(user:User)
        fun setProvinces(provinces: List<String>)
        fun setDistricts(districts: List<String>)
        fun setWards(wards: List<String>)
        fun navigateGoogleMap()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadUsers()
        fun loadProvinces()
        fun onProvinceSelected(provinceName: String)
        fun onDistrictSelected(districtName: String)
    }
}