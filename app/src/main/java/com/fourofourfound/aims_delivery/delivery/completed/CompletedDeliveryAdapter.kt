package com.fourofourfound.aims_delivery.delivery.completed


import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.database.entities.BillOfLadingImages
import com.fourofourfound.aims_delivery.database.relations.CompletedFormWithInfo
import com.fourofourfound.aims_delivery.delivery.completed.CompletedDeliveryAdapter.ViewHolder
import com.fourofourfound.aims_delivery.deliveryForms.finalForm.BillOfLadingAdapter
import com.fourofourfound.aims_delivery.deliveryForms.finalForm.BitmapListListener
import com.fourofourfound.aims_delivery.utils.getDate
import com.fourofourfound.aims_delivery.utils.getTime
import com.fourofourfound.aims_delivery.utils.htmlToText
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.delivery_summary.view.*
import uk.co.senab.photoview.PhotoViewAttacher


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
            addBoldWithText(
                bill_of_lading,
                "Bill Of Lading: ",
                (item.form.billOfLadingNumber ?: "Not Provided").toString()
            )
            addBoldWithText(product_desc, "Product: ", item.product.productDesc!!)
            addBoldWithText(net_qty, "Net Quantity: ", item.form.netQty.toString())
            addBoldWithText(gross_qty, "Gross Quantity: ", item.form.grossQty.toString())
            addBoldWithText(start_date, "Start Date: ", getDate(item.form.startTime))
            addBoldWithText(end_date, "End Date: ", getDate(item.form.endTime))
            addBoldWithText(start_time, "Start Time: ", getTime(item.form.startTime))
            addBoldWithText(end_time, "End Time: ", getTime(item.form.endTime))
            addBoldWithText(
                trailer_begin,
                "Trailer Reading: ",
                item.form.trailerBeginReading.toString()
            )
            addBoldWithText(
                trailer_end,
                "Trailer Reading: ",
                item.form.trailerEndReading.toString()
            )
            if (item.destination.wayPointTypeDescription != "Source") {
                addBoldWithText(
                    stick_reading_before,
                    "Stick Reading: ",
                    (item.form.stickReadingBefore ?: "Not Provided").toString()
                )
                addBoldWithText(
                    stick_reading_after,
                    "Stick Reading: ",
                    (item.form.stickReadingAfter ?: "Not Provided").toString()
                )
                addBoldWithText(
                    meter_reading_before,
                    "Meter Reading: ",
                    (item.form.meterReadingBefore ?: "Not Provided").toString()
                )
                addBoldWithText(
                    meter_reading_after,
                    "Meter Reading: ",
                    (item.form.meterReadingAfter ?: "Not Provided").toString()
                )
                site_readings.visibility = View.VISIBLE
            }
            val adapter = BillOfLadingAdapter(
                BitmapListListener(enlargeListener = { imageBitMap ->


                    var alertDialog =
                        AlertDialog.Builder(
                            context,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                    alertDialog.setView(R.layout.each_image_view)
                    var dialog = alertDialog.create()
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.image_to_display).apply {
                        setImageBitmap(imageBitMap)
                        PhotoViewAttacher(this).update()
                    }

                }), bill_of_ladingImages.context
            )
            adapter.submitList(getImagePath(item.images))
            bill_of_ladingImages.adapter = adapter
        }
    }

    private fun getImagePath(list: List<BillOfLadingImages>): List<String> {
        return list.map {
            it.imagePath
        }
    }

    private fun addBoldWithText(view: TextView, boldText: String, text: String) {
        val notesText = "<b> $boldText </b> $text"
        view.text = htmlToText(notesText)
    }


}



