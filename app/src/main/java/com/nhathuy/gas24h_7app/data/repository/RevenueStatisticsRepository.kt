package com.nhathuy.gas24h_7app.data.repository

import com.nhathuy.gas24h_7app.data.model.ProductSalesSummary
import com.nhathuy.gas24h_7app.data.model.RevenueStatistics
import java.util.Date

interface RevenueStatisticsRepository {
    suspend fun getWeeklyStats(weeks:Int): Result<RevenueStatistics>
    suspend fun getMonthlyStats(months:Int): Result<RevenueStatistics>
    suspend fun getYearlyStats(years:Int): Result<RevenueStatistics>
    suspend fun getTopSellingProducts(startDate:Date, endDate: Date, limit:Int=5): Result<List<ProductSalesSummary>>
}