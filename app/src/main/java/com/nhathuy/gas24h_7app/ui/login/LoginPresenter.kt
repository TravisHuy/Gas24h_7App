
package com.nhathuy.gas24h_7app.ui.login

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.adapter.Country
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import com.nhathuy.gas24h_7app.util.Constants.ADMIN_PHONE_NUMBER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val auth:FirebaseAuth,
                                         private val repository: CountryRepository,
                                         private val userRepository: UserRepository
):LoginContract.Presenter {

    private  var view: LoginContract.View?=null
    private lateinit var countries:List<Country>
    private lateinit var verificationId:String
    private var selectCountryCode= ""
    private var  fullPhoneNumber=""


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
            view?.showError("Please enter a valid phone number")
            return false
        }
        return true
    }


    override fun sendVerification(phoneNumber: String) {
        if(validatePhoneNumber(phoneNumber)){
            view?.showLoading()
            var formattedNumber=  formatPhoneNumber(phoneNumber)

            fullPhoneNumber=selectCountryCode+formattedNumber
            fullNumber(fullPhoneNumber)

            if(isAdminNumber(fullPhoneNumber)){
                handleAdminLogin(fullPhoneNumber)
                return
            }

            sendPhoneVerification(fullPhoneNumber)
        }
    }

    private fun sendPhoneVerification(fullPhoneNumber: String) {
        val options=PhoneAuthOptions.newBuilder(auth)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(view as Activity)
            .setPhoneNumber(fullPhoneNumber)
            .setCallbacks(object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    view?.hideLoading()
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    view?.hideLoading()
                    view?.showError("Verification failed: "+e.message)
                }

                override fun onCodeSent(id: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    view?.hideLoading()
                    this@LoginPresenter.verificationId=id
                    view?.navigateVerification(verificationId,fullPhoneNumber)
                }

            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser !=null
    }

    private fun isAdminNumber(fullPhoneNumber: String): Boolean {
        return fullPhoneNumber==ADMIN_PHONE_NUMBER
    }

    private fun formatPhoneNumber(phoneNumber: String): String{
        return if (phoneNumber.startsWith("0")) {
            phoneNumber.substring(1)
        } else {
            phoneNumber
        }
    }
    private fun fullNumber(fullNumber:String):String{
        return fullNumber
    }


    private fun signInWithCredential(credential: PhoneAuthCredential) {
        view?.showLoading()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                view?.hideLoading()
                if (task.isSuccessful) {
                    checkUserInDatabase()
                } else {
                    view?.showError("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun checkUserInDatabase() {
        val userId = auth.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userRepository.getUser(userId)
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { view?.navigateMainActivity() },
                        onFailure = { view?.navigateVerification(verificationId, fullPhoneNumber) }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Error checking user: ${e.message}")
                }
            }
        }
    }

    // admin
    private fun handleAdminLogin(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(view as Activity)
            .setPhoneNumber(phoneNumber)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    view?.hideLoading()
                    signInAdminWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    view?.hideLoading()
                    view?.showError("Admin verification failed: ${e.message}")
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    view?.hideLoading()
                    verificationId = id
                    view?.navigateVerification(verificationId, phoneNumber)
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInAdminWithCredential(credential: PhoneAuthCredential) {
        view?.showLoading()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkAndCreateAdminUser()
                } else {
                    view?.hideLoading()
                    view?.showError("Admin authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun checkAndCreateAdminUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val adminResult = userRepository.isUserAdmin()
                withContext(Dispatchers.Main) {
                    adminResult.fold(
                        onSuccess = { isAdmin ->
                            if (isAdmin) {
                                view?.navigateAdmin()
                            } else {
                                // Create admin user if it doesn't exist
                                createNewAdminUser()
                            }
                        },
                        onFailure = {
                            view?.showError("Error checking admin status: ${it.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Error: ${e.message}")
                }
            }
        }
    }

    private fun createNewAdminUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userRepository.createAdminUser(ADMIN_PHONE_NUMBER)
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = {
                            view?.hideLoading()
                            view?.navigateAdmin()
                        },
                        onFailure = {
                            view?.hideLoading()
                            view?.showError("Failed to create admin user: ${it.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Error creating admin user: ${e.message}")
                }
            }
        }
    }

}
