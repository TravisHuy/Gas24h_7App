package com.nhathuy.gas24h_7app.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.databinding.ItemAllQrCodeBinding

class AllQrCodeAdapter(private val onSave: (Int) -> Unit,
                       private val onShare: (Int) -> Unit,
                       private val onItemSelect: (Int) -> Unit
):RecyclerView.Adapter<AllQrCodeAdapter.AllQrCodeViewHolder>() {

    private var items: List<Triple<String, String, Bitmap>> = emptyList()
    private var selectedPositions = mutableSetOf<Int>()
    private var isSelectionMode = false


    inner class AllQrCodeViewHolder(val binding:ItemAllQrCodeBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Triple<String, String, Bitmap>, position: Int) {
            binding.apply {
                tvProductName.text = item.second
                ivQrCode.setImageBitmap(item.third)

                checkBox.visibility = if(isSelectionMode) View.VISIBLE else View.GONE
                checkBox.isChecked = selectedPositions.contains(position)


                checkBox.setOnClickListener {
                    onItemSelect(position)
                }

                root.setOnClickListener {
                    if(!isSelectionMode){
                        isSelectionMode =true
                        onItemSelect(position)
                        notifyDataSetChanged()
                    }
                    true
                }

                // Show/hide action buttons based on selection mode
                actionsGroup.visibility = if (isSelectionMode) View.GONE else View.VISIBLE

                if(!isSelectionMode){
                    btnSaveQr.setOnClickListener {
                        onSave(position)
                    }
                    btnShareQr.setOnClickListener {
                        onShare(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllQrCodeAdapter.AllQrCodeViewHolder {
        val binding = ItemAllQrCodeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllQrCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllQrCodeAdapter.AllQrCodeViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size


    fun updateData(newItems: List<Triple<String, String, Bitmap>>) {
        items = newItems
        notifyDataSetChanged()
    }


    fun toggleSelection(position: Int){
        if(selectedPositions.contains(position)){
            selectedPositions.remove(position)
        }else{
            selectedPositions.add(position)
        }
        notifyItemChanged(position)
    }

    fun clearSelections() {
        selectedPositions.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }
    fun getSelectedPositions() :Set<Int> = selectedPositions

    fun setSelectionMode(enabled:Boolean){
        isSelectionMode  = enabled
        if(!enabled){
            selectedPositions.clear()
        }
        notifyDataSetChanged()
    }
}