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

/**
 * Load info adapter
 * This adapter provides access to the load information and creates a view for
 * each item in the data set.
 * @constructor Create empty Load info adapter
 */
class LoadInfoAdapter : RecyclerView.Adapter<LoadInfoAdapter.ViewHolder>() {
    /**
     * Data
     * The list of destinations.
     */
    var data = listOf<SourceOrSite>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Trip
     * The current trip.
     */
    var trip: Trip? = null

    /**
     * Get item count
     *  Provides the total number of items in the data set held by the adapter.
     */
    override fun getItemCount() = data.size

    /**
     * View holder
     * This class represents how each item is structured in the adapter of the recycler view.
     * @constructor
     * @param itemView the view that is displayed by each item
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * On create view holder
     * Called when RecyclerView needs a new view holder of the given type to represent
     * an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.load_info_each_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Get item view type
     * Return the view type of the item at defined position for the purposes
     * of view recycling.
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at defined position.
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }

    /**
     * On bind view holder
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.itemView.sourceOrSiteInfo.apply {
            sourceOrSiteName.text = item.location.destinationName
            val fullAddress =
                "${item.location.address1.trim()}, ${item.location.city.trim()}, ${item.location.stateAbbrev.trim()} ${item.location.postalCode}"
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
        if (item.deliveryStatus == DeliveryStatusEnum.COMPLETED) {
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

    /**
     * Change destination icon color
     * This method changes the icon color acoording to the delivery status.
     * @param holder the view holder
     * @param item the destination whose status is to be checked
     */
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

    /**
     * Deep change text style
     * This method changes the text style according to the delivery status.
     * @param parent
     */
    private fun deepChangeTextStyle(parent: ViewGroup) {
        val typedValue = TypedValue();
        parent.context.theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        val color = ContextCompat.getColor(parent.context, typedValue.resourceId)
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


