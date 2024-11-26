package com.nhathuy.gas24h_7app.ui.setup_account

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class SetupAccountPresenter @Inject constructor(private val storage: FirebaseStorage,
                                                private val userRepository: UserRepository):SetupAccountContract.Presenter {
    private var view:SetupAccountContract.View?=null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private var currentAddress:String? = null
    private var currentImageUrl: String = ""

    override fun attachView(view: SetupAccountContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun updateProfileImage(imageUri: Uri) {
        coroutineScope.launch {
            try {
                val imageUrl = updateImageToFirebaseStorage(imageUri)
                val currentUser= userRepository.getCurrentUser().getOrThrow()

                currentImageUrl= imageUrl

                userRepository.updateUser(currentUser).getOrThrow()

                view?.showUpdateProfileImage(imageUrl)

                view?.showMessage("Profile image update successfully")
            }
            catch (e:Exception){
                view?.showError("Failed to update profile image: ${e.message}")
            }
        }
    }

    private suspend fun updateImageToFirebaseStorage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val filename = UUID.randomUUID().toString()
        val ref = storage.reference.child("profile_images/$filename")

        val uploadTask = ref.putFile(imageUri).await()
        return@withContext ref.downloadUrl.await().toString()
    }

    override fun loadUserInfo() {
        coroutineScope.launch {
            try {
                val result = userRepository.getUser(userRepository.getCurrentUserId()!!)
                result.fold(
                    onSuccess = { user ->
                        view?.showInfoUser(user)
                        currentAddress = user.address
                    },
                    onFailure = {e ->
                        view?.showMessage("Failed load user ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Failed load user ${e.message}")
            }
        }
    }

    override fun parseAndSetAddress(address: String) {
        val addressParts = address.split(", ")
        var province = ""
        var district = ""
        var ward = ""
        var houseNumber = ""

        when{
            addressParts.size >=4 ->{
                province = addressParts[addressParts.size-2]
                district = addressParts[addressParts.size-3]
                ward = addressParts[addressParts.size-4]
                houseNumber = addressParts.subList(0,addressParts.size-4).joinToString(", ")
            }
            addressParts.size==3 ->{
                province = addressParts[2]
                district = addressParts[1]
                ward = addressParts[0]
            }
            addressParts.size==2 ->{
                province = addressParts[1]
                district = addressParts[0]
            }
            addressParts.size==1 ->{
                province = addressParts[0]
            }
        }
        view?.setAddressFields(province, district, ward, houseNumber)
    }

    override fun getCurrentAddress(): String? {
        return currentAddress
    }

    override fun onSubmit(user: User) {
        coroutineScope.launch {
            try {
                val finalUser = if (currentImageUrl.isNotEmpty()) {
                    user.copy(imageUser = currentImageUrl)
                } else {
                    user
                }

                val result = userRepository.updateUser(finalUser)
                result.fold(
                    onSuccess = {
                        view?.onSubmitted()
                    },
                    onFailure = {
                            e ->
                        view?.showMessage("Cập nhật thông tin user thất bại: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }
}