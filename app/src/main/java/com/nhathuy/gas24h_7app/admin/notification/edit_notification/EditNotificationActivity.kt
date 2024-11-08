package com.nhathuy.gas24h_7app.admin.notification.edit_notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.admin.notification.theme.Gas24h_7AppTheme
import com.nhathuy.gas24h_7app.data.model.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditNotificationActivity : ComponentActivity(),EditNotificationContract.View {

    override var title by mutableStateOf("")
    override var content by mutableStateOf("")
    override var hotline by mutableStateOf("")

    private var currentNotification by mutableStateOf<Notification?>(null)
    private var showLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var successMessage by mutableStateOf<String?>(null)
    private var titleError by mutableStateOf<String?>(null)
    private var hotlineError by mutableStateOf<String?>(null)
    private var contentError by mutableStateOf<String?>(null)
    private var notificationId by mutableStateOf<String?>(null)
    private var selectedImageUri by mutableStateOf<Uri?>(null)
    private var currentImageBitmap by mutableStateOf<Bitmap?>(null)

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            currentImageBitmap = null
        }
    }


    @Inject
    lateinit var presenter: EditNotificationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        setContent {
            Gas24h_7AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditNotificationScreen()
                }
            }
        }
        presenter.attachView(this)
        notificationId = intent.getStringExtra("NOTIFICATION_ID")
        presenter.loadNotification(notificationId!!)
    }


    @Composable
    fun EditNotificationScreen(){
        var showPermissionDialog by remember { mutableStateOf(false) }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())) {

            OutlinedTextField(value = title,
                onValueChange = {title = it},
                label = { Text(text = "Title")},
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                isError = titleError !=null
            )
            titleError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedTextField(value = content,
                onValueChange = {content = it},
                label = { Text(text = "Content")},
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                isError = contentError !=null
            )
            contentError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedTextField(value = hotline,
                onValueChange = {hotline = it},
                label = { Text(text = "Hotline")},
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                isError = hotlineError !=null
            )
            hotlineError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Image Selection Button
            Button(
                onClick = { checkPermissionAndOpenImagePicker() },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Select Image")
            }

            // Image Preview
            ImagePreviewSection()


            if(showLoading){
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // Error Message
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Success Message
            successMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }


            // Submit Button
            Button(
                onClick = {
                        presenter.editNotification(notificationId!!, selectedImageUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Submit")
            }

        }

    }
    @Composable
    private fun ImagePreviewSection() {
        when {
            selectedImageUri != null -> {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            currentImageBitmap != null -> {
                AsyncImage(
                    model = currentImageBitmap,
                    contentDescription = "Current image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    private fun Uri.toBase64(context: Context): String? {
        return try {
            context.contentResolver.openInputStream(this)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                Base64.encodeToString(bytes, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
                showMessage("Permission denied")
            }
        }
    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview2() {
        Gas24h_7AppTheme {
            EditNotificationScreen()
        }
    }


    override fun showLoading() {
        showLoading = true
    }

    override fun hideLoading() {
        showLoading = false
    }

    override fun showSuccess(message: String) {
        successMessage = message
    }

    override fun showMessage(message: String) {
        errorMessage  = message
    }

    override fun showNotificationData(notification: Notification) {
        currentNotification = notification
        title = notification.title
        content = notification.content
        hotline = notification.hotline

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val imageData = notification.imageData
                if (imageData.isNotEmpty()) {
                    val decodedString = Base64.decode(imageData, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    withContext(Dispatchers.Main) {
                        currentImageBitmap = bitmap
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showMessage("Failed to load image")
                }
            }
        }
    }

    override fun showTitleError(message: String?) {
        titleError = message
    }

    override fun showContentError(message: String?) {
       contentError = message
    }

    override fun showHotlineError(message: String?) {
        hotlineError = message
    }

    override fun showImageError() {
        showMessage("Failed to process image")
    }

    override fun navigateAllNotification() {
        onBackPressed()
    }

    override fun clear() {
        title = ""
        content = ""
        hotline = ""
        selectedImageUri = null
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    Gas24h_7AppTheme {
        Greeting3("Android")
    }
}