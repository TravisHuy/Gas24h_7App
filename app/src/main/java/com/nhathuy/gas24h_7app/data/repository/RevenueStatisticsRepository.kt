package com.nhathuy.gas24h_7app.data.repository

import com.nhathuy.gas24h_7app.data.model.ProductSalesSummary
import com.nhathuy.gas24h_7app.data.model.RevenueStatistics
import java.util.Date

interface RevenueStatisticsRepository {
    suspend fun getDailyStats():Result<RevenueStatistics>
    suspend fun getWeeklyStats(): Result<RevenueStatistics>
    suspend fun getMonthlyStats(): Result<RevenueStatistics>
    suspend fun getYearlyStats(): Result<RevenueStatistics>
    suspend fun getTopSellingProducts(startDate:Date, endDate: Date, limit:Int=5): Result<List<ProductSalesSummary>>
}