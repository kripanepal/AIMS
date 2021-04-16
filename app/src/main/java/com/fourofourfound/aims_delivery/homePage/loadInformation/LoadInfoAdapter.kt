package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.load_info_each_item.view.*
import kotlinx.android.synthetic.main.source_or_site_info.view.*


class LoadInfoAdapter : RecyclerView.Adapter<LoadInfoAdapter.ViewHolder>() {
    var data = listOf<SourceOrSite>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.load_info_each_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.itemView.sourceOrSiteInfo.apply {
            sourceOrSiteName.text = item.location.destinationName
            address.text = item.location.address1
            product_desc.text = item.productInfo.productDesc
            product_qty.text = item.productInfo.requestedQty.toString() + " " + item.productInfo.uom

        }

        holder.itemView.load_notes.text = item.productInfo.fill


        if (item.wayPointTypeDescription != "Source") {
            holder.itemView.container_name.apply {
                text = item.siteContainerCode
                visibility = View.VISIBLE
            }
            holder.itemView.container_desc.apply {
                text = item.siteContainerDescription
                visibility = View.VISIBLE
            }
            holder.itemView.destinationImage.setImageResource(R.drawable.ic_site)
        }

        holder.itemView.statusImage.apply {
            if (item.status == StatusEnum.ONGOING)
                setImageResource(R.drawable.ongoing)
            else if (item.status == StatusEnum.COMPLETED) setImageResource(R.drawable.trip_done_icon)
        }


    }


}