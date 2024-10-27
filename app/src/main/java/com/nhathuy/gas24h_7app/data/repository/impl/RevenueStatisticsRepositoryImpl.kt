package com.nhathuy.gas24h_7app.data.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.OrderStatus
import com.nhathuy.gas24h_7app.data.model.ProductSalesSummary
import com.nhathuy.gas24h_7app.data.model.RevenuePeriod
import com.nhathuy.gas24h_7app.data.model.RevenueStatistics
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import com.nhathuy.gas24h_7app.data.repository.RevenueStatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class RevenueStatisticsRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val productRepository: ProductRepository
) : RevenueStatisticsRepository {

    //Hàm lấy thống kê doanh thu hành tuần trong số tuần chỉ định
    override suspend fun getWeeklyStats(weeks: Int): Result<RevenueStatistics> =
        withContext(Dispatchers.IO) {
            try {
                val endDate = Calendar.getInstance()
                val startDate = Calendar.getInstance().apply {
                    add(Calendar.WEEK_OF_YEAR,-weeks)
                }
                getStats(startDate.time,endDate.time,Calendar.WEEK_OF_YEAR)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    //Hàm lấy thống kê doanh thu hành tháng trong số tháng chỉ định
    override suspend fun getMonthlyStats(months: Int): Result<RevenueStatistics> = withContext(Dispatchers.IO){
        try {
            val endDate = Calendar.getInstance()
            val startDate = Calendar.getInstance().apply {
                add(Calendar.MONTH,months)
            }
            getStats(startDate.time,endDate.time,Calendar.MONTH)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Hàm lấy thống kê doanh thu hành năm
    override suspend fun getYearlyStats(years: Int): Result<RevenueStatistics> = withContext(Dispatchers.IO){
        try {
            val endDate = Calendar.getInstance()
            val startDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, -years)
            }
            getStats(startDate.time, endDate.time, Calendar.YEAR)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Hàm để lấy các sản phẩm bán chạy nhất trong một khoảng thời gian xác định với số lượng giới hạn
    override suspend fun getTopSellingProducts(
        startDate: Date,
        endDate: Date,
        limit: Int
    ): Result<List<ProductSalesSummary>>  = withContext(Dispatchers.IO){
        try {

            val orders = db.collection("orders")
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .whereIn("status", listOf(OrderStatus.DELIVERED.name, OrderStatus.RATED.name))
                .get()
                .await()

            val productSales = mutableMapOf<String, ProductSalesSummary>()

            orders.documents.forEach {
                doc ->
                doc.toObject(Order::class.java)?.let {
                    order ->
                    order.items.forEach {
                        item->
                        productSales.merge(
                            item.productId,
                            ProductSalesSummary(
                                productId = item.productId,
                                productName = "",
                                quantitySold = item.quantity,
                                revenue = item.price * item.quantity
                            )
                        ){ existing, new ->
                            existing.copy(
                                quantitySold = existing.quantitySold + new.quantitySold,
                                revenue = existing.revenue + new.revenue
                            )
                        }
                    }
                }
            }

            //lấy productName
            val products = productRepository.getProductByIds(productSales.keys.toList()).getOrNull() ?: emptyList()
            products.forEach { product ->
                productSales[product.id]?.let { summary ->
                    productSales[product.id] = summary.copy(productName = product.name)
                }
            }

            Result.success(
                productSales.values.sortedByDescending { it.revenue }.take(limit)
            )
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    // Hàm phụ trợ để tính toán và trả về thống kê doanh thu cho một khoảng thời gian
    private suspend fun getStats(startDate: Date, endDate: Date, periodField: Int): Result<RevenueStatistics> {
        val orders = db.collection("orders")
            .whereGreaterThanOrEqualTo("createdAt",startDate)
            .whereLessThanOrEqualTo("createdAt",endDate)
            .whereIn("status", listOf(OrderStatus.DELIVERED.name,OrderStatus.RATED.name))
            .get()
            .await()

        val orderList = orders.documents.mapNotNull { it.toObject(Order::class.java) }

        //  // Tính toán các giá trị tổng
        val totalRevenue  = orderList.sumOf { it.totalAmount }
        val totalOrders = orderList.size
        val totalProductSold = orderList.sumOf { order ->
            order.items.sumOf { it.quantity }
        }

        // Nhóm các đơn hàng theo chu kỳ (tuần/tháng/năm) để tính doanh thu theo chu kỳ
        val calendar = Calendar.getInstance()
        val revenueByPeriod = orderList.groupBy { order ->
            calendar.time = order.createdAt

            when(periodField){
                Calendar.WEEK_OF_YEAR ->calendar.get(Calendar.WEEK_OF_YEAR).toString()+"-"+calendar.get(Calendar.YEAR)
                Calendar.MONTH -> SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(order.createdAt)
                Calendar.YEAR -> calendar.get(Calendar.YEAR).toString()
                else->""
            }
        }.map {
            (period,orders) ->
            RevenuePeriod(
                period=period,
                revenue = orders.sumOf { it.totalAmount },
                orderCount = orders.size,
                productCount = orders.sumOf { order-> order.items.sumOf { it.quantity } },
                date = orders.first().createdAt
            )
        }.sortedBy { it.date }

        // lấy ra những sản phẩm bán chạy
        val topProducts = getTopSellingProducts(startDate,endDate).getOrNull() ?: emptyList()

        return Result.success(
            RevenueStatistics(
                totalRevenue = totalRevenue,
                totalOrders = totalOrders,
                totalProductSold = totalProductSold,
                revenueByPeriod = revenueByPeriod,
                topSellingProducts = topProducts
            )
        )
    }
}