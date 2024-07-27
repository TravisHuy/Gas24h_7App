package com.nhathuy.gas24h_7app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class LimitedArrayAdapter(
    context: Context,
    resource: Int,
    objects: MutableList<String>
):ArrayAdapter<String>(context,resource,objects){
    private var initialLimit = Int.MAX_VALUE
    private var showingAllItems = false

    fun setInitialLimit(limit: Int) {
        initialLimit = limit
        showingAllItems = false
        notifyDataSetChanged()
    }

    fun showAllItems() {
        showingAllItems = true
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (showingAllItems) super.getCount()
        else minOf(initialLimit, super.getCount())
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)

        if (!showingAllItems && position == initialLimit - 1 && super.getCount() > initialLimit) {
            (view as TextView).text = "Show more..."
        }

        return view
    }

    override fun getItem(position: Int): String? {
        return if (!showingAllItems && position == initialLimit - 1 && super.getCount() > initialLimit) {
            "Show more..."
        } else {
            super.getItem(position)
        }
    }
}