package com.fourofourfound.aims_delivery.delivery.completed


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.database.entities.DatabaseCompletionForm
import com.fourofourfound.aims_delivery.database.relations.CompletedFormWithInfo
import com.fourofourfound.aims_delivery.delivery.completed.CompletedDeliveryAdapter.ViewHolder
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.utils.htmlToText
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.delivery_summary.view.*
import kotlinx.android.synthetic.main.load_info_each_item.view.*


class CompletedDeliveryAdapter : RecyclerView.Adapter<ViewHolder>() {

    var data = listOf<CompletedFormWithInfo>()
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

            holder.itemView.apply {
                destination_name.text = item.location.destinationName
                addBoldWithText(wayPoint_type, "Type: ", item.destination.wayPointTypeDescription)
                addBoldWithText(bill_of_lading, "Bill Of Lading: ", (item.form.billOfLadingNumber?:"Not Provided").toString())
                addBoldWithText(product_desc, "Product: ", item.form.product)
                addBoldWithText(net_qty, "Net Quantity: ", item.form.netQty.toString())
                addBoldWithText(gross_qty, "Gross Quantity: ", item.form.grossQty.toString())
                addBoldWithText(start_date, "Start Date: ", item.form.startDate)
                addBoldWithText(end_date, "End Date: ", item.form.endDate)
                addBoldWithText(start_time, "Start Time: ", item.form.startTime)
                addBoldWithText(end_time, "End Time: ", item.form.endTime)
                addBoldWithText(trailer_begin, "Trailer Reading: ", item.form.trailerBeginReading.toString())
                addBoldWithText(trailer_end, "Trailer Reading: ", item.form.trailerEndReading.toString())
                if(item.destination.wayPointTypeDescription != "Source"){
                    addBoldWithText(stick_reading_before, "Stick Reading: ", (item.form.stickReadingBefore?:"Not Provided").toString())
                    addBoldWithText(stick_reading_after, "Stick Reading: ", (item.form.stickReadingAfter?:"Not Provided").toString())
                    addBoldWithText(meter_reading_before, "Meter Reading: ", (item.form.meterReadingBefore?:"Not Provided").toString())
                    addBoldWithText(meter_reading_after, "Meter Reading: ", (item.form.meterReadingAfter?:"Not Provided").toString())
                    site_readings.visibility = View.VISIBLE
                }
            }
        }

    private fun addBoldWithText(view: TextView, boldText: String, text: String){
        val notesText = "<b> $boldText </b> $text"
        view.text = htmlToText(notesText)
    }




    }



