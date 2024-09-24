
package com.nhathuy.gas24h_7app.ui.verify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.databinding.ActivityVerificationBinding
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import com.nhathuy.gas24h_7app.ui.register.RegisterActivity
import javax.inject.Inject

class VerificationActivity : AppCompatActivity(),VerificationContract.View {

    private lateinit var binding:ActivityVerificationBinding

    @Inject
    lateinit var presenter: VerificationPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val verificationId=intent.getStringExtra("verificationId") ?:""
        val phoneNumber=intent.getStringExtra("fullPhoneNumber") ?:""

        presenter.setVerificationId(verificationId)
        presenter.setPhoneNumber(phoneNumber)

        setupOtpInputs()
        setupButtonSend()
    }

    private fun setupOtpInputs() {
        val editTexts = listOf(
            binding.edVerificationOtp1,
            binding.edVerificationOtp2,
            binding.edVerificationOtp3,
            binding.edVerificationOtp4,
            binding.edVerificationOtp5,
            binding.edVerificationOtp6
        )

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                    updateOtp()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editTexts[i].text.isNullOrEmpty() && i > 0) {
                        editTexts[i - 1].apply {
                            setText("")
                            requestFocus()
                        }
                    }
                }
                false
            }
        }
    }

    private fun updateOtp() {
        presenter.onOtpChanged(getOtpInput())
    }

    private fun setupButtonSend() {
        binding.btnSend.setOnClickListener {
            presenter.verifyOtp()
        }
        binding.changePhonenumber.setOnClickListener {
            presenter.setNavigateLogin()
        }
        binding.resendOtp.setOnClickListener {
            presenter.handleResendClick()
        }
    }

    override fun showLoading() {
        binding.verificationProgressbar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.verificationProgressbar.visibility=View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun navigateRegister() {
        startActivity(Intent(this,RegisterActivity::class.java))
        finish()
    }

    override fun navigateLogin() {
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    override fun setOtpInput(otp: String) {
        otp.forEachIndexed { index, char ->
            when(index){
                0 -> binding.edVerificationOtp1.setText(char.toString())
                1 -> binding.edVerificationOtp2.setText(char.toString())
                2 -> binding.edVerificationOtp3.setText(char.toString())
                3 -> binding.edVerificationOtp4.setText(char.toString())
                4 -> binding.edVerificationOtp5.setText(char.toString())
                5 -> binding.edVerificationOtp6.setText(char.toString())
            }
        }
    }

    override fun getOtpInput(): String {
        return  binding.edVerificationOtp1.text.toString() +
                binding.edVerificationOtp2.text.toString() +
                binding.edVerificationOtp3.text.toString() +
                binding.edVerificationOtp4.text.toString() +
                binding.edVerificationOtp5.text.toString() +
                binding.edVerificationOtp6.text.toString()
    }

    override fun setPhoneNumber(phoneNumber: String) {
        binding.tvVerPhoneNumber.text=phoneNumber
    }

    override fun enableVerifyButton(enable: Boolean) {
        binding.btnSend.isEnabled=enable
    }

    override fun showResendButton() {
        binding.resendOtp.visibility=View.VISIBLE
        binding.resendOtp.isEnabled=true
    }

    override fun hideResendButton() {
        binding.resendOtp.isEnabled=false
    }

    override fun updateResendButtonText(text: String) {
        binding.resendOtp.text = text
    }

    override fun navigateMain() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
