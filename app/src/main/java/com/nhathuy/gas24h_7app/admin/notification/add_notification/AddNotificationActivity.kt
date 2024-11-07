package com.nhathuy.gas24h_7app.admin.notification.add_notification

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.databinding.ActivityAddNotificationBinding
import javax.inject.Inject

class AddNotificationActivity : AppCompatActivity(), AddNotificationContract.View {
    private lateinit var binding :ActivityAddNotificationBinding

    @Inject
    lateinit var presenter: AddNotificationPresenter

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d("AddNotificationActivity", "Selected image URI: $it")
            selectedImageUri = it
            binding.ivPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)
        setUpViews()
    }

    private fun setUpViews() {
        binding.btnSelectImage.setOnClickListener {
            checkPermissionAndOpenImagePicker()
        }

        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()
            val hotline = binding.etHotline.text.toString().trim()

            presenter.addNotification(title,content,selectedImageUri,hotline)
        }
    }
    private fun checkPermissionAndOpenImagePicker() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                // Show an explanation to the user
                Toast.makeText(this, "Permission is needed to select an image", Toast.LENGTH_LONG).show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false
    }

    override fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSubmit.isEnabled = true
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showImageError() {
        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
    }

    override fun clear() {
        binding.etTitle.setText("")
        binding.etContent.setText("")
        binding.etHotline.setText("")
        binding.ivPreview.setImageDrawable(null)
        selectedImageUri = null
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}