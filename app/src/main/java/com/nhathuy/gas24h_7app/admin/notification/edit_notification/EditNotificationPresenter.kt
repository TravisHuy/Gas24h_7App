package com.nhathuy.gas24h_7app.admin.notification.edit_notification

import android.content.Context
import android.net.Uri
import android.util.Log
import com.nhathuy.gas24h_7app.data.repository.NotificationRepository
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

class EditNotificationPresenter @Inject constructor(
    private val context: Context,
    private val notificationRepository: NotificationRepository
) : EditNotificationContract.Presenter {
    private var view: EditNotificationContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: EditNotificationContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadNotification(id: String) {
        coroutineScope.launch {
            view?.showLoading()
            try {
                notificationRepository.getNotificationById(id).collect { result ->
                    result.fold(
                        onSuccess = { notification ->
                            view?.showNotificationData(notification)
                            view?.title = notification.title
                            view?.content = notification.content
                            view?.hotline = notification.hotline
                        },
                        onFailure = { e ->
                            view?.showMessage("Loaded notification error ${e.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                view?.showMessage("Loaded notification error ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    override fun editNotification(id: String, imageUri: Uri?) {
        coroutineScope.launch {
            view?.showLoading()
            try {
                if (!validateInputs(view)) {
                    return@launch
                }

                val titlePart = view?.title?.toRequestBody("text/plain".toMediaTypeOrNull())
                val contentPart = view?.content?.toRequestBody("text/plain".toMediaTypeOrNull())
                val hotlinePart = view?.hotline?.toRequestBody("text/plain".toMediaTypeOrNull())

                // Convert Uri to File and create MultipartBody.Part
                val imageMultipart = imageUri?.let { uri ->
                    try {
                        val file = getFileFromUri(context, uri)
                        if (file != null && file.exists()) {
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("imageFile", file.name, requestFile)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("EditNotificationPresenter", "Error processing image: ${e.message}")
                        view?.showImageError()
                        null
                    }
                }

                notificationRepository.editNotification(
                    id, titlePart!!, contentPart!!, imageMultipart, hotlinePart!!
                ).collect { result ->
                    result.fold(
                        onSuccess = {
                            view?.showSuccess("Edited Notification Successfully")
                            view?.clear()
                            view?.navigateAllNotification()
                        },
                        onFailure = { e ->
                            view?.showMessage("Edited notification error ${e.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                view?.showMessage("Edited notification error ${e.message}")
            } finally {
                view?.hideLoading()
            }
        }
    }

    private fun validateInputs(view: EditNotificationContract.View?): Boolean {
        var isValid = true

        if (view?.title.isNullOrBlank()) {
            view?.showTitleError("Title is required")
            isValid = false
        } else {
            view?.showTitleError(null)
        }

        if (view?.content.isNullOrBlank()) {
            view?.showContentError("Content is required")
            isValid = false
        } else {
            view?.showContentError(null)
        }

        if (view?.hotline.isNullOrBlank()) {
            view?.showHotlineError("Hotline is required")
            isValid = false
        } else {
            view?.showHotlineError(null)
        }

        return isValid
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
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
            Log.e("EditNotificationPresenter", "Error creating file from URI: ${e.message}")
            null
        }
    }
}