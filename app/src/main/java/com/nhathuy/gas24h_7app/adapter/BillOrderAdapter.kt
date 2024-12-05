package com.nhathuy.gas24h_7app.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Order
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.User
import com.nhathuy.gas24h_7app.databinding.BillItemLayoutBinding
import com.nhathuy.gas24h_7app.util.Constants
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import java.text.SimpleDateFormat
import java.util.Locale

class BillOrderAdapter(private var orders:List<Order> = emptyList(),
                       private var users: Map<String, User> = emptyMap(),
                       private var products: Map<String, Product> = emptyMap()
): RecyclerView.Adapter<BillOrderAdapter.BillOrderViewHolder>() {

    inner class BillOrderViewHolder(val binding:BillItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillOrderViewHolder {
        val binding = BillItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BillOrderViewHolder(binding)
    }

    override fun getItemCount(): Int  = orders.size

    override fun onBindViewHolder(holder: BillOrderViewHolder, position: Int) {
        val order = orders[position]
        val user = users[order.userId]

        with(holder.binding){
            order?.let {
                tvInformationOrderIds.text=order.id
                tvInformationOrderCount.text = holder.binding.root.context.getString(R.string.count_total_product,order.items.size)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                tvInformationOrderDate.text = dateFormat.format(order.createdAt)

                tvInformationOrderTotalAmount.text = NumberFormatUtils.formatPriceVN(order.totalAmount)

                user?.let {
                    tvBuyerName.text = user.fullName
                    tvBuyerAddress.text = user.address
                    tvBuyerPhone.text = user.phoneNumber
                    Log.d("TagPhone","${user.phoneNumber}")
                }

                generateQRCode(order.id,billQrcode)

                val informationOderItemAdapter = InformationOderItemAdapter(order.items,products)
                recyclerOrderInformation.layoutManager = LinearLayoutManager(holder.itemView.context)
                recyclerOrderInformation.adapter = informationOderItemAdapter
            }
        }
    }
    private fun generateQRCode(orderId: String, imageView: ImageView) {
        try {
            val qrContent = "ORDERS:$orderId"
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(
                qrContent,
                BarcodeFormat.QR_CODE,
                200,
                200
            )
            val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)
            for (x in 0 until 200) {
                for (y in 0 until 200) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                    )
                }
            }
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun updateData(
        newOrders: List<Order>,
        newUsers: Map<String, User>,
        newProducts: Map<String, Product>
    ) {
        orders = newOrders
        users = newUsers
        products = newProducts
        notifyDataSetChanged()
    }
}