package com.nhathuy.gas24h_7app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.DiscountType
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.Voucher
import com.nhathuy.gas24h_7app.databinding.VoucherItemBinding
import com.nhathuy.gas24h_7app.util.NumberFormatUtils
import com.nhathuy.travishuyprogressbar.max
import com.nhathuy.travishuyprogressbar.progress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooseVoucherAdapter( private val context:Context,
                            private var vouchers:List<Voucher> = listOf(),
                            private var selectedItemId:String? = null,
                            private var onItemChecked : (String,Boolean) ->Unit
):RecyclerView.Adapter<ChooseVoucherAdapter.ChooseVoucherViewHolder>() {

    inner class ChooseVoucherViewHolder(val binding:VoucherItemBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseVoucherAdapter.ChooseVoucherViewHolder {
        val binding = VoucherItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChooseVoucherViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChooseVoucherAdapter.ChooseVoucherViewHolder,
        position: Int
    ) {
        val voucher = vouchers[position]
        holder.binding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                voucher?.let {
                    when (voucher.discountType) {
                        DiscountType.PERCENTAGE -> {
                            tvVoucherOfferpercent.text = context.getString(R.string.voucher_tv_offerpercent, voucher.discountValue)
                            tvVoucherDiscountPrice.visibility = View.GONE
                        }
                        DiscountType.FIXED_AMOUNT -> {
                            tvVoucherOfferpercent.visibility = View.GONE
                            tvVoucherDiscountPrice.visibility = View.VISIBLE
                            val formattedDiscount = NumberFormatUtils.formatDiscount(voucher.discountValue)
                            tvVoucherDiscountPrice.text = context.getString(R.string.voucher_discount_price, formattedDiscount)
                        }
                    }
                    val formattedMinOrderAmount = NumberFormatUtils.formatDiscount(it.minOrderAmount)
                    tvVoucherMinimumPrice.text=context.getString(R.string.tv_voucher_minimum_price,formattedMinOrderAmount)

                    tvVoucherCountItem.text="x${it.maxUsagePreUser}"

                    setupVoucherSelection(this@apply, voucher)
                    
                    progressBar.max = it.maxUsage
                    progressBar.progress = it.currentUsage

                    val usagePercentage = (it.currentUsage.toFloat() / it.maxUsage.toFloat() * 100).toInt()
                    tvVoucherCondition.text = context.getString(R.string.voucher_condition, usagePercentage)
                }
            }
        }
    }

    private fun setupVoucherSelection(binding: VoucherItemBinding, voucher: Voucher) {
        binding.voucherCheckbox.isChecked = selectedItemId  == voucher.id
        binding.voucherCheckbox.setOnCheckedChangeListener(null)

        val clickListener = View.OnClickListener {
            selectVoucher(voucher.id)
        }
        binding.root.setOnClickListener(clickListener)
        binding.voucherCheckbox.setOnClickListener(clickListener)
    }

    private fun selectVoucher(voucherId: String) {
        val isCurrentlySelected = selectedItemId == voucherId
        val  newSelectedItemId = if (isCurrentlySelected) null else voucherId

        if(newSelectedItemId !=selectedItemId){
            val oldSelectItemId = selectedItemId
            selectedItemId = newSelectedItemId

            oldSelectItemId?.let {
                notifyItemChanged(vouchers.indexOfFirst { it.id == oldSelectItemId })
            }
            notifyItemChanged(vouchers.indexOfFirst { it.id == voucherId })

            onItemChecked(voucherId, !isCurrentlySelected)
        }
    }

    override fun getItemCount(): Int =vouchers.size

    fun updateVouchers(newVoucher:List<Voucher>,newSelectedItemId:String?){
        vouchers = newVoucher
        selectedItemId= newSelectedItemId
        notifyDataSetChanged()
    }
}