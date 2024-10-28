package com.nhathuy.gas24h_7app.admin.revenue_statistics

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.admin.revenue_statistics.ui.theme.Gas24h_7AppTheme
import com.nhathuy.gas24h_7app.data.model.ProductSalesSummary
import com.nhathuy.gas24h_7app.data.model.RevenuePeriod
import com.nhathuy.gas24h_7app.data.model.RevenueStatistics
import javax.inject.Inject

class RevenueStatisticsActivity : ComponentActivity(),RevenueStatisticsContract.View {

    @Inject
    lateinit var presenter: RevenueStatisticsPresenter

    private var currentStats by mutableStateOf<RevenueStatistics?>(null)
    private var showLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var selectedPeriod by mutableStateOf("Daily")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as Gas24h_7Application).getGasComponent().inject(this)
        setContent {
            Gas24h_7AppTheme {
                // A surface container using the 'background' color from the theme
                RevenueStatisticsScreen()
            }
        }
        presenter.attachView(this)
        presenter.loadDailyStats()
    }

    override fun showLoading() {
        showLoading  = true
    }

    override fun hideLoading() {
        showLoading = false
    }

    override fun showError(message: String) {
       errorMessage = message
    }

    override fun displayDayStats(stats: RevenueStatistics) {
        currentStats = stats
    }

    override fun displayWeeklyStats(stats: RevenueStatistics) {
         currentStats =  stats
    }

    override fun displayMonthlyStats(stats: RevenueStatistics) {
        currentStats = stats
    }

    override fun displayYearlyStats(stats: RevenueStatistics) {
        currentStats = stats
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RevenueStatisticsScreen(){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {


            TopAppBar(
                title = { Text("Revenue Statistics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(elevation = 0.dp)
            )

            PeriodSelectionButtons(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { period ->
                    selectedPeriod = period
                    when (period) {
                        "Daily" -> presenter.loadDailyStats()
                        "Weekly" -> presenter.loadWeeklyStats()
                        "Monthly" -> presenter.loadMonthlyStats()
                        "Yearly" -> presenter.loadYearlyStats()
                    }
                }
            )

            //Main content
            if(showLoading){
                LoadingIndicator()
            }
            else {
                RevenueContent(currentStats)
            }

            // Error Handling
            errorMessage?.let { message ->
                ErrorDialog(
                    message = message,
                    onDismiss = { errorMessage = null }
                )
            }
        }
    }
    @Composable
    fun PeriodSelectionButtons(  selectedPeriod: String,
                                 onPeriodSelected: (String) -> Unit) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Daily","Weekly", "Monthly", "Yearly").forEach { period ->
                SelectablePeriodButton(
                    text = period,
                    isSelected = selectedPeriod == period,
                    onClick = { onPeriodSelected(period) }
                )
            }
        }
    }

    @Composable
    private fun SelectablePeriodButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
        Button(onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (isSelected) 6.dp else 2.dp
            ),
            modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(text = text,
                color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface )

        }
    }
    @Composable
    private fun LoadingIndicator() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun RevenueContent(stats: RevenueStatistics?) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            stats?.let { statistics ->
                item {
                    RevenueChart(statistics.revenueByPeriod)
                }

                item {
                    StatisticsSummaryCard(statistics)
                }

                item {
                    TopProductsCard(statistics.topSellingProducts)
                }
            }
        }
    }
    @Composable
    private fun RevenueChart(periods: List<RevenuePeriod>) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(), // Đảm bảo AndroidView lấp đầy Card
                factory = { context ->
                    LineChart(context).apply {
                        setupChart()
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { chart ->
                    updateChartData(chart, periods)
                }
            )
        }
    }
    @Composable
    private fun StatisticsSummaryCard(stats: RevenueStatistics) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Summary",
                    style = MaterialTheme.typography.titleLarge, // h6 được thay thế bằng titleLarge trong M3
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                StatisticRow(
                    label = "Total Revenue",
                    value = "₫${stats.totalRevenue.formatMoney()}",
                    icon = Icons.Rounded.MonetizationOn
                )

                StatisticRow(
                    label = "Total Orders",
                    value = stats.totalOrders.toString(),
                    icon = Icons.Rounded.ShoppingCart
                )

                StatisticRow(
                    label = "Products Sold",
                    value = stats.totalProductSold.toString(),
                    icon = Icons.Rounded.Inventory
                )
            }
        }
    }
    @Composable
    private fun StatisticRow(
        label: String,
        value: String,
        icon: ImageVector
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
    @Composable
    private fun TopProductsCard(products: List<ProductSalesSummary>) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Top Selling Products",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                products.forEach { product ->
                    ProductSaleRow(product)
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }

    @Composable
    fun ProductSaleRow(product: ProductSalesSummary) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${product.quantitySold} units sold",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                text = "₫${product.revenue.formatMoney()}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun ErrorDialog(
        message: String,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }

    private fun LineChart.setupChart() {
        // Thiết lập chung
        description.isEnabled = false
        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(true)
        setPinchZoom(true)
        setDrawGridBackground(false)

        // Điều chỉnh padding và margins
        setViewPortOffsets(128f, 30f, 45f, 95f)
        setExtraOffsets(10f, 20f, 10f, 10f)

        // Thiết lập trục X
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(true)
            gridColor = Color.LTGRAY
            gridLineWidth = 0.5f
            granularity = 1f
            labelRotationAngle = -45f
            textSize = 10f
            textColor = Color.BLACK
            setDrawAxisLine(true)
            axisLineWidth = 1f
            axisLineColor = Color.GRAY

            // Định dạng nhãn trục X
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return try {
                        when(selectedPeriod){
                            "Daily"-> {
                                "${value.toInt()}:00"
                            }
                            "Weekly" -> {
                                when(value.toInt()){
                                    0 -> "Mon"
                                    1 -> "Tue"
                                    2 -> "Wed"
                                    3 -> "Thu"
                                    4 -> "Fri"
                                    5 -> "Sat"
                                    6 -> "Sun"
                                    else -> ""
                                }
                            }
                            "Monthly" -> {
                                val period = currentStats?.revenueByPeriod?.getOrNull(value.toInt())?.period ?: ""
                                period.split("/").firstOrNull() ?: ""
                            }
                            "Yearly" -> {
                                when (value.toInt()) {
                                    0 -> "Jan"
                                    1 -> "Feb"
                                    2 -> "Mar"
                                    3 -> "Apr"
                                    4 -> "May"
                                    5 -> "Jun"
                                    6 -> "Jul"
                                    7 -> "Aug"
                                    8 -> "Sep"
                                    9 -> "Oct"
                                    10 -> "Nov"
                                    11 -> "Dec"
                                    else -> ""
                                }
                            }
                            else -> ""
                        }

                    } catch (e: Exception) {
                        ""
                    }
                }
            }
        }

        // Thiết lập trục Y bên trái
        axisLeft.apply {
            setDrawGridLines(true)
            gridColor = Color.LTGRAY
            gridLineWidth = 0.5f
            textColor = Color.BLACK
            textSize = 10f
            axisLineWidth = 1f
            axisLineColor = Color.GRAY
            setDrawZeroLine(true)
            // Định dạng giá trị trục Y
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when {
                        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000)
                        value >= 1_000 -> String.format("%.1fK", value / 1_000)
                        else -> value.toInt().toString()
                    }
                }
            }
            // Thiết lập số lượng nhãn trục Y
            setLabelCount(6, true)
        }

        // Tắt trục Y bên phải
        axisRight.isEnabled = false

        // Thiết lập chú thích
        legend.apply {
            textSize = 12f
            textColor = Color.BLACK
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            form = Legend.LegendForm.CIRCLE
            formSize = 8f
            xEntrySpace = 10f
            yEntrySpace = 5f
        }
    }

    private fun updateChartData(chart: LineChart, periods: List<RevenuePeriod>) {

        val entries = when (selectedPeriod) {
            "Daily" -> {
                (0..23).map { hour ->
                    val revenue = periods.find { it.period == "$hour:00" }?.revenue ?: 0.0
                    Entry(hour.toFloat(), revenue.toFloat())
                }
            }
            "Weekly" -> {
                (0..6).map { day ->
                    val revenue = periods.find {
                        it.period == when(day) {
                            0 -> "Mon"
                            1 -> "Tue"
                            2 -> "Wed"
                            3 -> "Thu"
                            4 -> "Fri"
                            5 -> "Sat"
                            6 -> "Sun"
                            else -> ""
                        }
                    }?.revenue ?: 0.0
                    Entry(day.toFloat(), revenue.toFloat())
                }
            }
            "Monthly" -> {
                periods.mapIndexed { index, period ->
                    Entry(index.toFloat(), period.revenue.toFloat())
                }
            }
            "Yearly" -> {
                (0..11).map { month ->
                    val revenue = periods.find {
                        it.period == when(month) {
                            0 -> "Jan"
                            1 -> "Feb"
                            2 -> "Mar"
                            3 -> "Apr"
                            4 -> "May"
                            5 -> "Jun"
                            6 -> "Jul"
                            7 -> "Aug"
                            8 -> "Sep"
                            9 -> "Oct"
                            10 -> "Nov"
                            11 -> "Dec"
                            else -> ""
                        }
                    }?.revenue ?: 0.0
                    Entry(month.toFloat(), revenue.toFloat())
                }
            }
            else -> emptyList()
        }

        val dataSet = LineDataSet(entries, "Revenue").apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            cubicIntensity = 0.2f

            // Định dạng đường
            color = getColor(R.color.red)
            lineWidth = 2f

            // Định dạng điểm
            setCircleColor(getColor(R.color.red))
            circleRadius = 4f
            circleHoleRadius = 2f
            setDrawCircleHole(true)
            circleHoleColor = Color.WHITE

            // Định dạng fill
            setDrawFilled(true)
            fillColor = getColor(R.color.red)
            fillAlpha = 30

            // Định dạng highlight
            highLightColor = Color.rgb(244, 117, 117)
            setDrawHorizontalHighlightIndicator(false)
            highlightLineWidth = 1.5f

            // Định dạng giá trị
            valueTextSize = 10f
            valueTextColor = Color.BLACK
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when {
                        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000)
                        value >= 1_000 -> String.format("%.1fK", value / 1_000)
                        else -> value.toInt().toString()
                    }
                }
            }
        }

        chart.apply {
            data = LineData(dataSet)

            // Thiết lập phạm vi hiển thị
            val visibleXRange = when (selectedPeriod) {
                "Daily" -> 12f    // Show 12 hours at a time
                "Weekly" -> 7f    // Show full week
                "Monthly" -> 10f  // Show 10 days at a time
                "Yearly" -> 12f   // Show full year
                else -> 7f
            }
            setVisibleXRangeMaximum(visibleXRange)

            // Animation
            animateY(1000)

            // Cập nhật và vẽ lại biểu đồ
            notifyDataSetChanged()
            invalidate()
        }
    }
    private fun Int.formatMoney(): String {
        return String.format("%,d", this)
    }

    private fun Double.formatMoney(): String {
        return String.format("%,.0f", this)
    }
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Gas24h_7AppTheme {
            RevenueStatisticsScreen()
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun GreetingHoNhat(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Anh Huy Phu Yen $name!",
        modifier = modifier
    )
}
