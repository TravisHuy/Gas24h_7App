
package com.nhathuy.gas24h_7app.ui.verify


interface VerificationContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message:String)
        fun navigateRegister()
        fun navigateLogin()
        fun setOtpInput(otp:String)
        fun getOtpInput():String
        fun setPhoneNumber(phoneNumber: String)
        fun enableVerifyButton(enable: Boolean)
        fun showResendButton()
        fun hideResendButton()
        fun updateResendButtonText(text: String)
        fun navigateMain()

    }
    interface Presenter{
        fun attachView(view: VerificationContract.View)
        fun detachView()
        fun setVerificationId(verificationId: String)
        fun setPhoneNumber(phoneNumber: String)
        fun setNavigateLogin()
        fun onOtpChanged(otp: String)
        fun verifyOtp()
        fun handleResendClick()
        fun startResendTimer()
    }
}
