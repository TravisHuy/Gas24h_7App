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
    //hàm lấy thống kê doanh thu ngày hiện tại
    override suspend fun getDailyStats(): Result<RevenueStatistics> = withContext(Dispatchers.IO){
        try {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY,0)
                set(Calendar.MINUTE,0)
                set(Calendar.SECOND,0)
                set(Calendar.MILLISECOND, 0)
            }
            val startDate = calendar.time

            calendar.add(Calendar.DAY_OF_MONTH, 1)
            calendar.add(Calendar.MILLISECOND, -1)
            val endDate  = calendar.time

            getStats(startDate,endDate,Calendar.HOUR_OF_DAY)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }

    //Hàm lấy thống kê doanh thu tuần hiện tại
    override suspend fun getWeeklyStats(): Result<RevenueStatistics> =
        withContext(Dispatchers.IO) {
            try {
                val calendar = Calendar.getInstance()

                //đặt về đầu tuần (thứ 2)
                calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
                calendar.set(Calendar.HOUR_OF_DAY,0)
                calendar.set(Calendar.MINUTE,0)
                calendar.set(Calendar.SECOND,0)
                calendar.set(Calendar.MILLISECOND,0)
                val startDate = calendar.time

                //đặt về cuối tuần
                calendar.add(Calendar.DAY_OF_WEEK,6)
                calendar.add(Calendar.HOUR_OF_DAY,23)
                calendar.add(Calendar.MINUTE,59)
                calendar.add(Calendar.SECOND,59)
                val endDate = calendar.time

                getStats(startDate,endDate,Calendar.DAY_OF_WEEK)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    //Hàm lấy thống kê doanh thu tháng hiện tại
    override suspend fun getMonthlyStats(): Result<RevenueStatistics> = withContext(Dispatchers.IO){
        try {
            val calendar = Calendar.getInstance()

            // Đặt về đầu năm
            calendar.set(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.time

            // Đặt về cuối năm
            calendar.add(Calendar.YEAR, 1)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.time

            // Chỉ gọi một lần với Calendar.MONTH
            getStats(startDate, endDate, Calendar.MONTH)


            getStats(startDate,endDate,Calendar.DAY_OF_MONTH)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Hàm lấy thống kê doanh thu hành năm
    override suspend fun getYearlyStats(): Result<RevenueStatistics> = withContext(Dispatchers.IO){
        try {
            val calendar = Calendar.getInstance()

            //đặt về đầu tuần (thứ 2)
            calendar.set(Calendar.DAY_OF_YEAR,1)
            calendar.set(Calendar.HOUR_OF_DAY,0)
            calendar.set(Calendar.MINUTE,0)
            calendar.set(Calendar.SECOND,0)
            calendar.set(Calendar.MILLISECOND,0)
            val startDate = calendar.time

            //đặt về cuối tuần
            calendar.add(Calendar.YEAR,1)
            calendar.add(Calendar.DAY_OF_YEAR,-1)
            calendar.add(Calendar.HOUR_OF_DAY,23)
            calendar.add(Calendar.MINUTE,59)
            calendar.add(Calendar.SECOND,59)
            val endDate = calendar.time


            getStats(startDate,endDate,Calendar.MONTH)
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
        // Khởi tạo calendar và định dạng ngày
        val calendar = Calendar.getInstance()
        val revenueByPeriod = when (periodField) {
            Calendar.HOUR_OF_DAY -> {
                // Thống kê theo giờ (0-23)
                (0..23).map { hour ->
                    val hourRevenue = orderList.filter { order ->
                        calendar.time = order.createdAt
                        calendar.get(Calendar.HOUR_OF_DAY) == hour
                    }
                    RevenuePeriod(
                        period = "$hour:00",
                        revenue = hourRevenue.sumOf { it.totalAmount },
                        orderCount = hourRevenue.size,
                        productCount = hourRevenue.sumOf { order -> order.items.sumOf { it.quantity } },
                        date = calendar.apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }.time
                    )
                }
            }

            Calendar.DAY_OF_WEEK -> {
                // Thống kê theo ngày trong tuần (Thứ 2 - Chủ nhật)
                val dayNames = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                (0..6).map { dayIndex ->
                    val dayRevenue = orderList.filter { order ->
                        calendar.time = order.createdAt
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        // Chuyển đổi từ Calendar.DAY_OF_WEEK sang index của mảng dayNames
                        (dayOfWeek + 5) % 7 == dayIndex // Để Monday = 0, Sunday = 6
                    }
                    RevenuePeriod(
                        period = dayNames[dayIndex],
                        revenue = dayRevenue.sumOf { it.totalAmount },
                        orderCount = dayRevenue.size,
                        productCount = dayRevenue.sumOf { order -> order.items.sumOf { it.quantity } },
                        date = calendar.apply {
                            set(Calendar.DAY_OF_WEEK, ((dayIndex + 1) % 7) + 1)
                        }.time
                    )
                }
            }

            Calendar.DAY_OF_MONTH -> {
                // Thống kê theo ngày trong tháng
                orderList.groupBy { order ->
                    calendar.time = order.createdAt
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(order.createdAt)
                }.map { (period, orders) ->
                    RevenuePeriod(
                        period = period,
                        revenue = orders.sumOf { it.totalAmount },
                        orderCount = orders.size,
                        productCount = orders.sumOf { order -> order.items.sumOf { it.quantity } },
                        date = orders.first().createdAt
                    )
                }.sortedBy { it.date }
            }

            Calendar.MONTH -> {
                // Thống kê theo tháng trong năm (Tháng 1-12)
                val monthNames = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )
                (0..11).map { month ->
                    val monthRevenue = orderList.filter { order ->
                        calendar.time = order.createdAt
                        calendar.get(Calendar.MONTH) == month
                    }
                    RevenuePeriod(
                        period = monthNames[month],
                        revenue = monthRevenue.sumOf { it.totalAmount },
                        orderCount = monthRevenue.size,
                        productCount = monthRevenue.sumOf { order -> order.items.sumOf { it.quantity } },
                        date = calendar.apply {
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, 1)
                        }.time
                    )
                }
            }

            else -> emptyList()
        }
        // lấy ra những sản phẩm bán chạy
        val topProducts = getTopSellingProducts(startDate,endDate,5).getOrNull() ?: emptyList()

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