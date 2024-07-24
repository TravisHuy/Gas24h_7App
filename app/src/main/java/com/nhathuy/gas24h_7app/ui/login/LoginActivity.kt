package com.nhathuy.gas24h_7app.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.Country
import com.nhathuy.gas24h_7app.adapter.CountrySpinnerAdapter
import com.nhathuy.gas24h_7app.data.repository.CountryRepository
import com.nhathuy.gas24h_7app.databinding.ActivityLoginBinding
import com.nhathuy.gas24h_7app.ui.verify.VerificationActivity
import javax.inject.Inject

class LoginActivity : AppCompatActivity(),LoginContract.View {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var countryAdapter:CountrySpinnerAdapter

    @Inject
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.setView(this)


        setupCountrySpinner()
        setupSubmitButton()
    }

    private fun setupCountrySpinner() {
        val countries =listOf(
            Country("Vietnam", "+84", R.drawable.ic_flag_vietnam),
            Country("Usa", "+1", R.drawable.ic_flag_usa),
            Country("Thai Lan", "+66", R.drawable.ic_flag_thailan),
        )
        countryAdapter=CountrySpinnerAdapter(this, countries)
        binding.countrySpinner.adapter=countryAdapter


        binding.countrySpinner.onItemSelectedListener= object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, positon: Int, id: Long) {
                val selectCountry =countries[positon]
                presenter.onCountrySelected(selectCountry)
                binding.edLoginPhoneNumber.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }
    private fun setupSubmitButton() {
        binding.btnContinue.setOnClickListener {
            val phoneNumber= binding.edLoginPhoneNumber.text.toString()
            presenter.sendVerification(phoneNumber)
        }
    }
    override fun showLoading() {
        binding.loginProgressbar.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        binding.loginProgressbar.visibility=View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    override fun navigateVerification(verificationId: String) {
        val intent=Intent(this,VerificationActivity::class.java)
        intent.putExtra("verificationId",verificationId)
        startActivity(intent)
        finish()
    }
}