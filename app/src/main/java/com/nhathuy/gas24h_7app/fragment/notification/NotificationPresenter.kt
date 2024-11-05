package com.nhathuy.gas24h_7app.fragment.notification

import com.nhathuy.gas24h_7app.data.api.NotificationApiService
import com.nhathuy.gas24h_7app.data.api.NotificationRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationPresenter @Inject constructor(@NotificationRetrofit private val notificationApiService: NotificationApiService):NotificationContract.Presenter {
    private var view:NotificationContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)
    override fun attachView(view: NotificationContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadNotifications() {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val response = notificationApiService.getAllNotification()
                if(response.isSuccessful){
                    val notifications = response.body() ?: emptyList()
                    view?.showNotifications(notifications)
                }
                else{
                    view?.showMessage("Error: ${response.code()}")
                }
            }
            catch (e:Exception){
                view?.showMessage("Failed load notification ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }
}