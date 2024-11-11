package com.nhathuy.gas24h_7app.admin

import android.util.Log
import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdminPresenter @Inject constructor(private val userRepository: UserRepository):AdminContract.Presenter{
    private var view:AdminContract.View? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)


    override fun attachView(view: AdminContract.View) {
        this.view = view
        checkAdminPermission()
    }

    override fun getAdmin(){
        coroutineScope.launch {
            try {
                val result = userRepository.getCurrentUserId()!!
                view?.showMessage("$result")
            }
            catch (e:Exception){
                view?.showMessage("Failed ${e.message}")
            }
        }
    }



    override fun checkAdminPermission() {
        coroutineScope.launch {
            try {
                val result = userRepository.getUserAdminId()
                result.onSuccess { adminId ->
                    val currentUserId = userRepository.getCurrentUserId()

                    // Log để kiểm tra
                    Log.d("AdminCheck", "Admin ID: $adminId")
                    Log.d("AdminCheck", "Current User ID: $currentUserId")

                    if (currentUserId != adminId) {
                        Log.d("AdminCheck", "Not admin access")
                        view?.showMessage("Bạn không có quyền truy cập")
                        view?.navigateToMain()
                    } else {
                        Log.d("AdminCheck", "Admin access successful")
                    }
                }.onFailure {
                    Log.e("AdminCheck", "Error checking admin: ${it.message}")
                    view?.showMessage("Lỗi kiểm tra quyền admin: ${it.message}")
                    view?.navigateToMain()
                }
            } catch (e: Exception) {
                Log.e("AdminCheck", "Exception: ${e.message}")
                view?.showMessage("Lỗi: ${e.message}")
                view?.navigateToMain()
            }
        }
    }

    override fun detachView() {
        view = null
    }
}