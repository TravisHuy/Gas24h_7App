package com.nhathuy.gas24h_7app.admin.notification.add_notification

import android.content.Context
import android.net.Uri
import android.util.Log
import com.nhathuy.gas24h_7app.data.helper.NotificationHelper
import com.nhathuy.gas24h_7app.data.repository.NotificationRepository
import com.nhathuy.gas24h_7app.fragment.notification.NotificationContract
import com.nhathuy.gas24h_7app.websocket.WebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class AddNotificationPresenter @Inject constructor(private val  context: Context,
                                                   private val notificationRepository: NotificationRepository,
                                                   private val notificationHelper: NotificationHelper,
                                                   private val webSocketService: WebSocketService
):AddNotificationContract.Presenter{
    private var view:AddNotificationContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)


    override fun attachView(view: AddNotificationContract.View) {
        this.view= view
    }
    override fun detachView() {
        view = null
        job.cancel()
        webSocketService.disconnect()
    }

    override fun addNotification(title: String, content: String, imageUri: Uri?, hotline: String) {
        if (title.isEmpty() || content.isEmpty() || hotline.isEmpty()) {
            view?.showMessage("Please fill all required fields")
            return
        }
        view?.showLoading()

        // Connect to WebSocket first
        webSocketService.connectForNotification(
            onConnected = {
                // After successful connection, proceed with adding notification
                uploadNotification(title, content, imageUri, hotline)
            },
            onError = { error ->
                view?.hideLoading()
                view?.showMessage("Failed to connect to notification service: $error")
            }
        )
    }

    private fun uploadNotification(title: String, content: String, imageUri: Uri?, hotline: String) {
        if(title.isEmpty() || content.isEmpty() || hotline.isEmpty()){
            view?.showMessage("Please fill all required fields")
            return
        }
        view?.showLoading()
        coroutineScope.launch {
            try {
                val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())
                val hotlinePart = hotline.toRequestBody("text/plain".toMediaTypeOrNull())

                // Convert Uri to File and create MultipartBody.Part
                val imageMultipart = imageUri?.let { uri ->
                    try {
                        val file = getFileFromUri(uri)
                        if (file != null && file.exists()) {
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("imageFile", file.name, requestFile)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("AddNotificationPresenter", "Error processing image: ${e.message}")
                        view?.showImageError()
                        null
                    }
                }

                notificationRepository.addNotification(
                    title = titlePart,
                    content = contentPart,
                    imageFile = imageMultipart,
                    hotline = hotlinePart
                ).collect { result ->
                    result.onSuccess {
                        notificationHelper.showNotification(title,content, hotline)
                        view?.showMessage("Notification added successfully")
                        view?.clear()
                    }.onFailure { exception ->
                        view?.hideLoading()
                        view?.showMessage(exception.message ?: "Failed to add notification")
                    }
                }

            }
            catch (e:Exception){
                view?.showMessage(e.message ?: "An error occurred")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(
                context.cacheDir,
                "upload_image_${System.currentTimeMillis()}.jpg"
            )

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            Log.e("AddNotificationPresenter", "Error creating file from URI: ${e.message}")
            null
        }
    }

}