package com.nhathuy.gas24h_7app.admin.revenue_statistics

import com.nhathuy.gas24h_7app.data.model.RevenueStatistics

interface RevenueStatisticsContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun displayDayStats(stats: RevenueStatistics)
        fun displayWeeklyStats(stats: RevenueStatistics)
        fun displayMonthlyStats(stats: RevenueStatistics)
        fun displayYearlyStats(stats: RevenueStatistics)
    }
    interface Presenter{
        fun attachView(view:View)
        fun detachView()
        fun loadDailyStats()
        fun loadWeeklyStats()
        fun loadMonthlyStats()
        fun loadYearlyStats()
    }
}