
package com.nhathuy.gas24h_7app.ui.splash

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.ActivitySplashBinding
import com.nhathuy.gas24h_7app.ui.login.LoginActivity
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject


class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    private var currentProgress =0
    private val maxProgress =100
    private val progressUpdateInterval = 50L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupProgressBar()
        startProgressAnimation()
    }
    private fun setupProgressBar() {
        binding.progressBar.apply {
            setMax(maxProgress)
            setProgress(0)
            setStartColor(Color.parseColor("#0D47A1"))
            setEndColor(Color.parseColor("#2196F3"))
            setBackgroundColor(Color.parseColor("#E3F2FD"))
            setCornerRadius(4f)


            val gradientColors = intArrayOf(
                Color.parseColor("#0D47A1"),
                Color.parseColor("#1976D2"),
                Color.parseColor("#2196F3")
            )
            setGradientColors(gradientColors)
        }
    }
    private fun startProgressAnimation() {
        val handler = Handler(Looper.getMainLooper())
        val progressIncrement = maxProgress * progressUpdateInterval / 1500

        val runnable = object : Runnable {
            override fun run() {
                if (currentProgress < maxProgress) {
                    currentProgress += progressIncrement.toInt()
                    binding.progressBar.setProgress(currentProgress)
                    handler.postDelayed(this, progressUpdateInterval)
                } else {
                    // Progress complete, start MainActivity
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                }
            }
        }

        handler.post(runnable)
    }
}
