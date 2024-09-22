package com.nhathuy.gas24h_7app.fragment.profile

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ProfilePresenter @Inject constructor(private val storage: FirebaseStorage,private val userRepository: UserRepository):ProfileContract.Presenter {

    private var view:ProfileContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: ProfileContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
    }

    override fun updateProfileImage(imageUri: Uri) {
       coroutineScope.launch {
           try {
                val imageUrl = updateImageToFirebaseStorage(imageUri)
               val currentUser= userRepository.getCurrentUser().getOrThrow()

               currentUser.imageUser = imageUrl

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
}