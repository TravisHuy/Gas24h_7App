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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooseVoucherAdapter( private val context:Context,
                            private var vouchers:List<Voucher> = listOf(),
                            private var selectedItemIds:Set<String> = setOf(),
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
                    if (it.discountType==DiscountType.FIXED_AMOUNT){
                        tvVoucherOfferpercent.text=context.getString(R.string.voucher_tv_offerpercent,it.discountValue)
                        tvVoucherMaxDiscount.text=context.getString(R.string.voucher_tv_max_discount,it.discountValue)
                    }
                    else{
                        tvVoucherDiscountPrice.text=context.getString(R.string.voucher_tv_offerpercent,it.discountValue)
                        tvVoucherOfferpercent.visibility=View.GONE
                        tvVoucherMaxDiscount.visibility=View.GONE
                        tvVoucherDiscountPrice.visibility=View.VISIBLE
                    }
                    tvVoucherMinimumPrice.text=context.getString(R.string.tv_voucher_minimum_price,it.minOrderAmount)
                    tvVoucherCondition.text=context.getString(R.string.tv_voucher_minimum_price,it.minOrderAmount)
                    tvVoucherCountItem.text="x${it.maxUsagePreUser}"

                    voucherCheckbox.isChecked=selectedItemIds.contains(it.id)
                    voucherCheckbox.setOnCheckedChangeListener { _, isChecked ->
                        onItemChecked(it.id,isChecked)
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int =vouchers.size

    fun updateVouchers(newVoucher:List<Voucher>,newSelectedItemIds:Set<String>){
        vouchers = newVoucher
        selectedItemIds= newSelectedItemIds
        notifyDataSetChanged()
    }
}