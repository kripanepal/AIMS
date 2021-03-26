package com.fourofourfound.aims_delivery.homePage


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.fourofourfound.aims_delivery.database.utilClasses.Fuel_with_info
import com.fourofourfound.aimsdelivery.R


class FuelSummaryAdapter(context: Context, dataList: List<Fuel_with_info>) :
    ArrayAdapter<Fuel_with_info>(context, R.layout.fuel_summary_view, dataList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.fuel_summary_view, null)
            viewHolder = ViewHolder()
            viewHolder.fuel_type = convertView.findViewById<View>(R.id.fuel_type) as TextView
            viewHolder.fuel_source = convertView.findViewById<View>(R.id.fuel_source) as TextView
            viewHolder.site_count = convertView.findViewById<View>(R.id.site_count) as TextView
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        //you can use data.get(position) too
        val data: Fuel_with_info? = getItem(position)
        viewHolder.fuel_type.text = data?.fuel_type
        viewHolder.fuel_source.text = data?.fuel_source
        viewHolder.site_count.text = data?.site_count.toString()

        return convertView!!
    }

    inner class ViewHolder {
        lateinit var fuel_type: TextView
        lateinit var fuel_source: TextView
        lateinit var site_count: TextView
    }

    //constructor
}