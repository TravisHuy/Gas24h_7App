package com.nhathuy.gas24h_7app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.gas24h_7app.R

data class Country(val name:String,val code:String,val flagResId:Int)

class CountrySpinnerAdapter(context: Context,private var countries: List<Country>):ArrayAdapter<Country>(context,0,countries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent,false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent,true)
    }
    private fun createView(position: Int,recyclerView: View?,parent: ViewGroup,isDropDown:Boolean):View{
        val country = getItem(position)
        val view= recyclerView ?: LayoutInflater.from(context).inflate(R.layout.country_spinner_item,parent,false)

        country?.let {
            val flagImg=view.findViewById<ImageView>(R.id.flag_image)
            val countryName= view.findViewById<TextView>(R.id.country_name)
            val countryCode= view.findViewById<TextView>(R.id.country_code)

            flagImg.setImageResource(it.flagResId)


            if(isDropDown){
                countryName.visibility=View.VISIBLE
                countryName.text=it.name
                countryCode.visibility=View.VISIBLE
                countryCode.text=it.code
            }
            else{
                countryName.visibility=View.GONE
                countryCode.visibility=View.GONE
            }
        }


        return view
    }
}