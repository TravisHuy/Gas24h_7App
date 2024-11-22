package com.nhathuy.gas24h_7app.fragment.delete

import com.nhathuy.gas24h_7app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteAccountPresenter @Inject constructor(private val userRepository: UserRepository) :DeleteAccountContract.Presenter{

    private var view:DeleteAccountContract.View? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun attachView(view: DeleteAccountContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun deleteAccount() {
        coroutineScope.launch {
            try {
                userRepository.deleteUser().fold(
                    onSuccess = {
                        view?.showMessage("Xóa tài khoản thành công")
                        view?.navigateMain()
                    },
                    onFailure = { e->
                        view?.showMessage("Lỗi xóa tài khoản: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showMessage("${e.message} lỗi xóa tài khoản")
            }
        }
    }

}