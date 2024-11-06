package com.nhathuy.gas24h_7app.admin.notification.all_notification

import com.nhathuy.gas24h_7app.data.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllNotificationPresenter @Inject constructor(private val notificationRepository: NotificationRepository):AllNotificationContract.Presenter{

    private var view:AllNotificationContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)


    override fun attachView(view: AllNotificationContract.View) {
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

                notificationRepository.getAllNotifications().collect{
                    result ->
                    result.fold(
                        onSuccess = {
                            notifications ->
                            view?.showAllNotifications(notifications)
                        },
                        onFailure = {error ->
                            view?.showMessage(error.message ?: "Unknown error")
                        }
                    )
                }

            }catch (e:Exception){
                view?.showMessage(e.message ?: "Unexpected error")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun deleteNotification(id: String) {
        coroutineScope.launch {
            try {
                notificationRepository.deleteNotification(id).collect{
                    result ->
                    result.fold(
                        onSuccess = {
                            view?.showMessage("Deleted notification successfully")
                            loadNotifications()
                        },
                        onFailure = {
                            e->
                            view?.showMessage(e.message ?: "Unknown error")
                        }
                    )
                }
            }
            catch (e:Exception){
                view?.showMessage(e.message ?: "Delete Failed")
            }
        }
    }


}