package com.fourofourfound.aims_delivery.delivery.completed


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.delivery.completed.CompletedDeliveryAdapter.ViewHolder
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.delivery_summary.view.*


class CompletedDeliveryAdapter : RecyclerView.Adapter<ViewHolder>() {

    var data = listOf<SourceOrSite>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.delivery_summary, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = data[position]

            holder.itemView.sourceOrSiteInfo.text = item.wayPointTypeDescription
            holder.itemView.status.text = item.status.status.toString()
            holder.itemView.bill_of_lading.text = "11111"
            holder.itemView.product_desc.text = item.productInfo.productDesc
            holder.itemView.start_time.text = "Start Time"



        }




    }



