package com.nhathuy.gas24h_7app.ui.register

import android.content.Context
import android.util.Log
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

import javax.inject.Inject


class RegisterPresenter @Inject constructor(private val locationApiService: LocationApiService,
                                            private val userRepository: UserRepository,
                                            private val coroutineScope: CoroutineScope
):RegisterContract.Presenter {

    private var view:RegisterContract.View?=null
    private lateinit var provinces: List<Province>
    private lateinit var districts: Map<String,List<District>>
    private lateinit var wards: Map<String,List<Ward>>


    override fun attachView(view: RegisterContract.View) {
        this.view=view
    }

    override fun detachView() {
        view=null
    }

    override fun registerUser(user: User) {

    }

    override fun loadProvinces() {
        coroutineScope.launch {
            try {
                val response= withContext(Dispatchers.IO){
                    locationApiService.getProvides()
                }
                provinces=response.data
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
                val response= withContext(Dispatchers.IO){
                    locationApiService.getDistricts(province.id)
                }
                districts= mapOf(province.id to response.data)
                view?.setDistricts(response.data.map { it.full_name })
            }
            catch (e:Exception){
                view?.showError("Error fetching districts: ${e.message}")
            }
        }
    }

    override fun onDistrictSelected(districtName: String) {
        val district =districts.values.flatten().find { it.full_name==districtName }?:return
        coroutineScope.launch {
            try {
                val response= withContext(Dispatchers.IO){
                    locationApiService.getWards(district.id)
                }
                wards= mapOf(district.id to response.data)
                view?.setWards(response.data.map { it.full_name })
            }
            catch (e:Exception){
                view?.showError("Error fetching districts: ${e.message}")
            }
        }
    }

}