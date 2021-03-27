package com.fourofourfound.aims_delivery.loadInformation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.item_view.view.*


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
        val view = layoutInflater.inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.itemView.sourceOrSiteName.text = item.destinationName
        holder.itemView.address.text = item.address1
        holder.itemView.product_desc.text = item.productDesc
        holder.itemView.product_qty.text = item.requestedQty.toString() + " " + item.uom
        holder.itemView.load_notes.text = item.fill

        if (item.waypointTypeDescription != "Source") {
            holder.itemView.container_name.apply {
                text = item.siteContainerCode
                visibility = View.VISIBLE
            }
            holder.itemView.container_desc.apply {
                text = item.siteContainerDescription
                visibility = View.VISIBLE
            }
        }
    }





}