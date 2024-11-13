
package com.nhathuy.gas24h_7app.ui.verify

import android.app.Activity
import android.os.CountDownTimer
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VerificationPresenter @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : VerificationContract.Presenter {

    private var view: VerificationContract.View? = null
    private var verificationId: String = ""
    private var phoneNumber: String = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var countDownTimer: CountDownTimer? = null
    private var canResend = true

    override fun attachView(view: VerificationContract.View) {
        this.view = view
        startResendTimer()
    }

    override fun detachView() {
        view = null
        countDownTimer?.cancel()
    }

    override fun setVerificationId(verificationId: String) {
        this.verificationId = verificationId
    }

    override fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        view?.setPhoneNumber(phoneNumber)
    }

    override fun onOtpChanged(otp: String) {
        view?.enableVerifyButton(otp.length == 6)
    }

    override fun verifyOtp() {
        val otp = view?.getOtpInput() ?: return
        if (otp.length != 6) {
            view?.showError("Please enter a valid 6-digit OTP")
            return
        }

        view?.showLoading()
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneCredential(credential)
    }

    override fun handleResendClick() {
        if (canResend) {
            resendOtp()
        }
    }

    override fun startResendTimer() {
        canResend = false
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                view?.updateResendButtonText("Resend OTP in ${secondsRemaining}s")
            }

            override fun onFinish() {
                canResend = true
                view?.showResendButton()
                view?.updateResendButtonText("Resend OTP")
            }
        }.start()
    }

    override fun setNavigateLogin() {
        view?.navigateLogin()
    }

    private fun resendOtp() {
        view?.showLoading()
        view?.hideResendButton()

        val activity = view as? Activity ?: run {
            view?.hideLoading()
            view?.showError("Unable to resend OTP: Activity not available")
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        resendToken?.let { token ->
            options.setForceResendingToken(token)
        }

        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            view?.hideLoading()
            signInWithPhoneCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            view?.hideLoading()
            view?.hideResendButton()
            view?.showError("Verification failed: ${e.message}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@VerificationPresenter.verificationId = verificationId
            this@VerificationPresenter.resendToken = token
            view?.hideLoading()
            view?.showError("OTP sent successfully")
            startResendTimer()
        }
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                view?.hideLoading()
                if (task.isSuccessful) {
                    checkUserExistence()
                } else {
                    view?.showError("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun checkUserExistence() {
        val userId = auth.currentUser?.uid ?: return
        view?.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = userRepository.getUser(userId)
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = {   checkIfAdminAndNavigate(it)  },
                        onFailure = { view?.navigateRegister() }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.showError("Error checking user: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                }
            }
        }
    }

    private suspend fun checkIfAdminAndNavigate(user: User) {
        if (user == null) {
            view?.navigateRegister()
            return
        }

        try {
            val adminResult = userRepository.isUserAdmin()
            adminResult.fold(
                onSuccess = { isAdmin ->
                    if (isAdmin) {
                        view?.navigateAdmin()
                    } else {
                        view?.navigateMain()
                    }
                },
                onFailure = {
                    view?.showError("Error checking admin status: ${it.message}")
                }
            )
        } catch (e: Exception) {
            view?.showError("Error: ${e.message}")
        }
    }
}