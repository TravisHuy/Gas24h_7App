package com.nhathuy.gas24h_7app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.databinding.VoucherItemBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import com.nhathuy.travishuyprogressbar.max
import com.nhathuy.travishuyprogressbar.progress

class ChooseVoucherAdapter(
    private val context: Context,
    private var vouchers: List<Voucher> = listOf(),
    private var selectedItemId: String? = null,
    private val onItemChecked: (String, Boolean) -> Unit
) : RecyclerView.Adapter<ChooseVoucherAdapter.ChooseVoucherViewHolder>() {

    inner class ChooseVoucherViewHolder(val binding: VoucherItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(voucher: Voucher) {
            binding.apply {
                when (voucher.discountType) {
                    DiscountType.PERCENTAGE -> {
                        tvVoucherOfferpercent.text = context.getString(R.string.voucher_tv_offerpercent, voucher.discountValue)
                        tvVoucherOfferpercent.visibility = View.VISIBLE
                        tvVoucherDiscountPrice.visibility = View.GONE
                    }
                    DiscountType.FIXED_AMOUNT -> {
                        tvVoucherOfferpercent.visibility = View.GONE
                        tvVoucherDiscountPrice.visibility = View.VISIBLE
                        val formattedDiscount = NumberFormatUtils.formatDiscount(voucher.discountValue)
                        tvVoucherDiscountPrice.text = context.getString(R.string.voucher_discount_price, formattedDiscount)
                    }
                }

                val formattedMinOrderAmount = NumberFormatUtils.formatDiscount(voucher.minOrderAmount)
                tvVoucherMinimumPrice.text = context.getString(R.string.tv_voucher_minimum_price, formattedMinOrderAmount)

                tvVoucherCountItem.text = "x${voucher.maxUsagePreUser}"

                progressBar.max = voucher.maxUsage
                progressBar.progress = voucher.currentUsage

                val usagePercentage = (voucher.currentUsage.toFloat() / voucher.maxUsage.toFloat() * 100).toInt()
                tvVoucherCondition.text = context.getString(R.string.voucher_condition, usagePercentage)

                // Update the checkbox state
                voucherCheckbox.isChecked = selectedItemId == voucher.id
                // Set click listeners
                root.setOnClickListener { selectVoucher(voucher.id) }
                voucherCheckbox.setOnClickListener { selectVoucher(voucher.id) }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseVoucherViewHolder {
        val binding = VoucherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseVoucherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseVoucherViewHolder, position: Int) {
        holder.bind(vouchers[position])
    }

    override fun getItemCount(): Int = vouchers.size

    private fun selectVoucher(voucherId: String) {
        val isCurrentlySelected = selectedItemId == voucherId
        val newSelectedItemId = if (isCurrentlySelected) null else voucherId

        if (newSelectedItemId != selectedItemId) {
            val oldSelectedItemId = selectedItemId
            selectedItemId = newSelectedItemId

            oldSelectedItemId?.let {
                notifyItemChanged(vouchers.indexOfFirst { it.id == oldSelectedItemId })
            }
            notifyItemChanged(vouchers.indexOfFirst { it.id == voucherId })

            onItemChecked(voucherId, !isCurrentlySelected)
        }
    }

    fun updateVouchers(newVouchers: List<Voucher>, newSelectedItemId: String?) {
        vouchers = newVouchers
        selectedItemId = newSelectedItemId
        notifyDataSetChanged()
    }
}