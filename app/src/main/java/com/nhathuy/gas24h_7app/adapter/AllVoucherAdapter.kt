package com.nhathuy.gas24h_7app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.databinding.AllVoucherItemBinding
import com.nhathuy.gas24h_7app.databinding.BuyBackItemBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import com.nhathuy.travishuyprogressbar.max
import com.nhathuy.travishuyprogressbar.progress
import kotlinx.coroutines.withContext

class AllVoucherAdapter(private var vouchers: List<Voucher> = listOf()):RecyclerView.Adapter<AllVoucherAdapter.AllVoucherViewHolder>() {

    inner class AllVoucherViewHolder(val binding:AllVoucherItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllVoucherAdapter.AllVoucherViewHolder {
        val binding = AllVoucherItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllVoucherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllVoucherAdapter.AllVoucherViewHolder, position: Int) {
        val voucher = vouchers[position]

        with(holder.binding){
            voucher?.let {
                when (voucher.discountType) {
                    DiscountType.PERCENTAGE -> {
                        tvVoucherOfferpercent.text = holder.itemView.context.getString(R.string.voucher_tv_offerpercent, it.discountValue)
                        tvVoucherOfferpercent.visibility = View.VISIBLE
                        tvVoucherDiscountPrice.visibility = View.GONE
                    }
                    DiscountType.FIXED_AMOUNT -> {
                        tvVoucherOfferpercent.visibility = View.GONE
                        tvVoucherDiscountPrice.visibility = View.VISIBLE
                        val formattedDiscount = NumberFormatUtils.formatDiscount(it.discountValue)
                        tvVoucherDiscountPrice.text = holder.itemView.context.getString(R.string.voucher_discount_price, formattedDiscount)
                    }
                }
                val formattedMinOrderAmount = NumberFormatUtils.formatDiscount(it.minOrderAmount)
                tvVoucherMinimumPrice.text = holder.itemView.context.getString(R.string.tv_voucher_minimum_price, formattedMinOrderAmount)


                tvVoucherCountItem.text = "x${it.maxUsage}"

                progressBar.max = it.maxUsage
                progressBar.progress = it.currentUsage

                val usagePercentage = (it.currentUsage.toFloat() / it.maxUsage.toFloat() * 100).toInt()
                tvVoucherCondition.text = holder.itemView.context.getString(R.string.voucher_condition, usagePercentage)
            }
        }
    }

    override fun getItemCount(): Int = vouchers.size


    fun updateData(newVoucher:List<Voucher>){
        vouchers=newVoucher
        notifyDataSetChanged()
    }

}