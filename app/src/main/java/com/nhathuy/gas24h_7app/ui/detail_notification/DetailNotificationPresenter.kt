package com.nhathuy.gas24h_7app.ui.detail_notification

import com.nhathuy.gas24h_7app.data.api.NotificationApiService
import com.nhathuy.gas24h_7app.data.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailNotificationPresenter @Inject constructor(private val notificationRepository: NotificationRepository):DetailNotificationContract.Presenter{

    private var view:DetailNotificationContract.View?  =null

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: DetailNotificationContract.View) {
        this.view = view
    }

    override fun detachView() {
        view =null
        job.cancel()
    }

    override fun loadNotificationDetail(id: String) {
        coroutineScope.launch {
            try {
                val result = notificationRepository.getNotificationById(id)

                result.collect{
                        result ->
                    result.fold(
                        onSuccess = {
                            notification->
                            view?.showNotification(notification)
                        },
                        onFailure = {
                            e->
                            view?.showError("Failed load notification: ${e.message}")
                        }
                    )
                }
            }
            catch (e:Exception){
                view?.showError("Failed load notification: ${e.message}")
            }
        }
    }

}