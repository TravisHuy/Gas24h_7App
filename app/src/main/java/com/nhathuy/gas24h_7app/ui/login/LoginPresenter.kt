package com.nhathuy.gas24h_7app.ui.login

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nhathuy.gas24h_7app.adapter.Country
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val auth:FirebaseAuth,private val repository: CountryRepository):LoginContract.Presenter {

    private  var view: LoginContract.View?=null
    private lateinit var countries:List<Country>
    private lateinit var verificationId:String
    private var selectCountryCode= ""


    override fun attachView(view: LoginContract.View) {
        this.view=view
        loadCountries()
    }

    override fun detachView() {
        view=null
    }

    override fun loadCountries() {
        countries = repository.getCountries()
        view?.showCountries(countries)
    }

    override fun onCountrySelected(country: Country) {
        selectCountryCode=country.code
    }

    private fun validatePhoneNumber(phoneNumber: String):Boolean {
        if(phoneNumber.isEmpty()||phoneNumber.length<9){
            view?.showError("Invalid phone number")
            return false
        }
       return true
    }


    override fun sendVerification(phoneNumber: String) {
        if(validatePhoneNumber(phoneNumber)){
            view?.showLoading()
            var formattedNumber=  formatPhoneNumber(phoneNumber)

            val fullPhoneNumber=selectCountryCode+formattedNumber

            val options=PhoneAuthOptions.newBuilder(auth)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(view as Activity)
                .setPhoneNumber(fullPhoneNumber)
                .setCallbacks(object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        view?.hideLoading()
                        signWithPhoneCredential(phoneAuthCredential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        view?.hideLoading()
                        view?.showError("Verification failed: "+e.message)
                    }

                    override fun onCodeSent(id: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        view?.hideLoading()
                        verificationId=id
                        view?.navigateVerification(verificationId)
                    }

                }).build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String{
        return if (phoneNumber.startsWith("0")) {
            phoneNumber.substring(1)
        } else {
            phoneNumber
        }
    }

    private fun signWithPhoneCredential(phoneAuthCredential: PhoneAuthCredential) {
        view?.showLoading()
        auth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    view?.navigateVerification(verificationId)
                }
                else{
                    view?.showError("Auth failed: ${task.exception?.message}")
                }
            }
    }


}