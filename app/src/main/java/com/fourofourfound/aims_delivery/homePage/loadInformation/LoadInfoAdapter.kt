package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aims_delivery.utils.htmlToText
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.load_info_each_item.view.*
import kotlinx.android.synthetic.main.source_or_site_info.view.*


class LoadInfoAdapter() : RecyclerView.Adapter<LoadInfoAdapter.ViewHolder>() {
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

        if (item.status == StatusEnum.ONGOING) {
            holder.itemView.parentConstraint.apply {
                deepChangeTextColor(this)

            }
        }

        holder.itemView.sourceOrSiteInfo.apply {
            sourceOrSiteName.text = item.location.destinationName
            address.text = item.location.address1
            product_desc.text = item.productInfo.productDesc
            val qtyText = "${item.productInfo.requestedQty.toString()} ${item.productInfo.uom}"
            product_qty.text = qtyText

        }

        val notesText = "<b> Notes: </b> " + item.productInfo.fill
        holder.itemView.load_notes.text = htmlToText(notesText)

        if (position == data.size - 1) {
            holder.itemView.progressLine.visibility = View.GONE
        }

        if (item.wayPointTypeDescription != "Source") {
            holder.itemView.container_name.apply {
                val containerCode = "<b> Container: </b> " + item.siteContainerCode
                text = htmlToText(containerCode)
                visibility = View.VISIBLE
            }
            holder.itemView.container_desc.apply {
                val containerDesc = "<b> Description: </b> " + item.siteContainerDescription
                text = htmlToText(containerDesc)
                visibility = View.VISIBLE
            }
            holder.itemView.destinationImage.setImageResource(R.drawable.ic_site)
        }
        changeDestinationIconColor(holder, item)


    }

    private fun changeDestinationIconColor(
        holder: ViewHolder,
        item: SourceOrSite
    ) {
            if (item.status == StatusEnum.COMPLETED) {
                val colorGreen = ContextCompat.getColor(
                    holder.itemView.destinationImage.context,
                    R.color.Green
                )
                holder.itemView.destinationImage.setColorFilter(colorGreen)
                holder.itemView.progressLine.setBackgroundColor(colorGreen)
            } else {
                val typedValue = TypedValue()
                holder.itemView.destinationImage.context.theme.resolveAttribute(
                    R.attr.colorOnPrimary,
                    typedValue,
                    true
                )
                holder.itemView.destinationImage.setColorFilter(typedValue.data)
            }


    }


    private fun deepChangeTextColor(parent: ViewGroup) {
        parent.children.forEach {
            if (it is TextView) {
                var textView: TextView = it
                textView.setTypeface(null, Typeface.BOLD);
            } else if (it is ViewGroup) {
                deepChangeTextColor(it)
            }
        }
    }

}
