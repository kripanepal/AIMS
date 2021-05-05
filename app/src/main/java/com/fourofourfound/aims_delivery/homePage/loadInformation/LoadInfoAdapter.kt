package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import com.fourofourfound.aims_delivery.utils.htmlToText
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.load_info_each_item.view.*
import kotlinx.android.synthetic.main.source_or_site_info.view.*


class LoadInfoAdapter : RecyclerView.Adapter<LoadInfoAdapter.ViewHolder>() {
    var data = listOf<SourceOrSite>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var trip: Trip? = null

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.load_info_each_item, parent, false)
        return ViewHolder(view)
    }



    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.itemView.sourceOrSiteInfo.apply {
            sourceOrSiteName.text = item.location.destinationName
            var fullAddress = "${item.location.address1.trim()}, ${item.location.city.trim()}, ${item.location.stateAbbrev.trim()} ${item.location.postalCode}"
            address.text = fullAddress
            product_desc.text = item.productInfo.productDesc
            val qtyText = "${item.productInfo.requestedQty.toString()} ${item.productInfo.uom}"
            product_qty.text = qtyText

            truck_text.text = item.truckInfo.truckDesc
            trailer_text.text = item.trailerInfo.trailerDesc
        }

        val notesText = "<b> Notes: </b> " + item.productInfo.fill
        holder.itemView.load_notes.text = htmlToText(notesText)



        if (position == data.size - 1) {
            holder.itemView.progressLine.visibility = View.GONE
        }

        if(item.deliveryStatus == DeliveryStatusEnum.COMPLETED){
            holder.itemView.show_form.also { view ->
                view.visibility = View.VISIBLE
                holder.itemView.show_form.setOnClickListener {
                    trip?.apply {
                        it.findNavController().navigate(
                            LoadInfoFragmentDirections.actionLoadInfoFragmentToCompletedDeliveryFragment(
                                this,
                                item.seqNum
                            )
                        )
                    }


                }
            }

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
        when (item.deliveryStatus) {
            DeliveryStatusEnum.COMPLETED -> {
                deepChangeTextStyle(holder.itemView.parentConstraint)
                val colorGreen = ContextCompat.getColor(
                    holder.itemView.destinationImage.context,
                    R.color.Green
                )
                holder.itemView.destinationImage.setColorFilter(colorGreen)
                holder.itemView.progressLine.setBackgroundColor(colorGreen)
            }
            DeliveryStatusEnum.ONGOING -> {
                deepChangeTextStyle(holder.itemView.parentConstraint)
                val colorOrange = ContextCompat.getColor(
                    holder.itemView.destinationImage.context,
                    R.color.Aims_Orange
                )
                holder.itemView.destinationImage.setColorFilter(colorOrange)
            }
            else -> {
                val typedValue = TypedValue()
                holder.itemView.destinationImage.context.theme.resolveAttribute(
                    R.attr.color,
                    typedValue,
                    true
                )
                holder.itemView.destinationImage.setColorFilter(typedValue.data)
            }
        }

    }


    private fun deepChangeTextStyle(parent: ViewGroup) {
        var typedValue = TypedValue();
        parent.context.theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        var color = ContextCompat.getColor(parent.context, typedValue.resourceId)

        parent.children.forEach {
            if (it is TextView && it.id != R.id.sourceOrSiteName)
                it.setTextColor(
                    color
                )
            else if (it is ViewGroup)
                deepChangeTextStyle(it)

        }
    }

}


