package com.nhathuy.gas24h_7app.ui.search

import com.nhathuy.gas24h_7app.viewmodel.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class SearchPresenter @Inject constructor(private val searchViewModel: SearchViewModel):SearchContract.Presenter{

    private var view:SearchContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: SearchContract.View) {
        this.view=view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

}