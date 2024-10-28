package com.nhathuy.gas24h_7app.admin.revenue_statistics

import android.util.Log
import com.nhathuy.gas24h_7app.data.repository.RevenueStatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class RevenueStatisticsPresenter @Inject constructor(private val revenueStatisticsRepository: RevenueStatisticsRepository):RevenueStatisticsContract.Presenter {

    private var view:RevenueStatisticsContract.View?= null
    private val job = SupervisorJob()
    private val coroutineScope= CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: RevenueStatisticsContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun loadDailyStats() {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = revenueStatisticsRepository.getDailyStats()
                result.fold(
                    onSuccess = { stats ->
                        view?.displayDayStats(stats)
                    },
                    onFailure = {
                            e->
                        view?.showError("Error load daily stat: ${e.message}")
                        Log.d("RevenueStatisticsPresenter","${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Error load daily stat: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun loadWeeklyStats() {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = revenueStatisticsRepository.getWeeklyStats()
                result.fold(
                    onSuccess = { stats ->
                        view?.displayWeeklyStats(stats)
                    },
                    onFailure = {
                        e->
                        view?.showError("Error load week stat: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Error load week stat: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun loadMonthlyStats() {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = revenueStatisticsRepository.getMonthlyStats()
                result.fold(
                    onSuccess = { stats ->
                        view?.displayMonthlyStats(stats)
                    },
                    onFailure = {
                            e->
                        view?.showError("Error load month stats: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Error load month stats: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }

    override fun loadYearlyStats() {
        coroutineScope.launch {
            try {
                view?.showLoading()
                val result = revenueStatisticsRepository.getYearlyStats()
                result.fold(
                    onSuccess = { stats ->
                        view?.displayYearlyStats(stats)
                    },
                    onFailure = {
                            e->
                        view?.showError("Error load year stats: ${e.message}")
                    }
                )
            }
            catch (e:Exception){
                view?.showError("Error load year stats: ${e.message}")
            }
            finally {
                view?.hideLoading()
            }
        }
    }
}