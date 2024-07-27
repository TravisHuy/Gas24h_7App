package com.nhathuy.gas24h_7app.ui.verify

import android.app.Activity
import android.os.CountDownTimer
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VerificationPresenter @Inject constructor(private val auth:FirebaseAuth,
                                                ):VerificationContract.Presenter{

    private var view:VerificationContract.View? =null
    private var verificationId: String =""
    private var phoneNumber: String = ""

    private var resendToken: PhoneAuthProvider.ForceResendingToken? =null
    private var countDownTimer:CountDownTimer? =null
    private var canResend = true

    override fun attachView(view: VerificationContract.View) {
        this.view=view
        startResendTimer()
    }

    override fun detachView() {
        view=null
        countDownTimer?.cancel()
    }

    override fun setVerificationId(verificationId: String) {
        this.verificationId=verificationId
    }

    override fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber=phoneNumber
        view?.setPhoneNumber(phoneNumber)
    }

    override fun setNavigateLogin() {
        view?.navigateLogin()
    }

    override fun onOtpChanged(otp: String) {
        view?.enableVerifyButton(otp.length==6)
    }

    override fun verifyOtp() {
        val otp=view?.getOtpInput()?:return
        if(otp.length!=6){
            view?.showError("Please enter a valid 6-digit OTP")
            return
        }
        view?.showLoading()
        val credential=PhoneAuthProvider.getCredential(verificationId,otp)
        signWithPhoneCredential(credential)
    }

    override fun handleResendClick() {
        if(canResend){
            resendOtp()
        }
    }

    override fun startResendTimer() {
        canResend=false
        countDownTimer?.cancel()
        countDownTimer=object :CountDownTimer(1500,1000){
            override fun onTick(milli: Long) {
                val secondsRemaining=(milli/1000).toInt()
                view?.updateResendButtonText("Resend OTP in ${secondsRemaining}s")
            }

            override fun onFinish() {
                canResend=true
                view?.showResendButton()
                view?.updateResendButtonText("Resend OTP")
            }

        }.start()
    }

    private fun resendOtp() {
        view?.showLoading()
        view?.hideResendButton()
        val activity = view as? Activity ?: run {
            view?.hideLoading()
            view?.showError("Unable to resend OTP: Activity not available")
            return
        }

        val phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        resendToken?.let { token ->
            phoneAuthOptions.setForceResendingToken(token)
        }

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions.build())
    }

    private fun signWithPhoneCredential(phoneAutCredential: PhoneAuthCredential) {
        auth.signInWithCredential(phoneAutCredential)
            .addOnCompleteListener {
                task ->
                view?.hideLoading()
                if(task.isSuccessful){
                    view?.navigateRegister()
                }
                else{
                    view?.showError("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    private val callbacks = object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            view?.hideLoading()
            signWithPhoneCredential(credential)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            view?.hideLoading()
            view?.hideResendButton()
            view?.showError("Auth failed ${p0.message}")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            this@VerificationPresenter.verificationId=verificationId
            this@VerificationPresenter.resendToken=token
            view?.hideLoading()
            view?.showError("OTP sent successfully")
            startResendTimer()
        }

    }

}