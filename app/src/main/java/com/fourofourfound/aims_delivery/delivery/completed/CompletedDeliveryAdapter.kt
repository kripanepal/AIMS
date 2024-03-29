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

/**
 * Completed delivery adapter
 * This adapter provides access to the form data items and creates a view for
 * each item in the data set.
 * @constructor Create empty Completed delivery adapter
 */
class CompletedDeliveryAdapter : RecyclerView.Adapter<ViewHolder>() {

    /**
     * Data
     * The list of completed form information
     */
    var data = listOf<CompletedFormWithInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = data.size

    /**
     * View holder
     * This class represents how each item is structured in the adapter of the recycler view.
     * @constructor
     * @param view the view that is displayed by each item
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

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
        val view = layoutInflater.inflate(R.layout.delivery_summary, parent, false)
        return ViewHolder(view)
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

            start_date.text =  getDate(item.form.startTime)
            end_date.text =  getDate(item.form.endTime)
            start_time.text =  getTime(item.form.startTime)
            end_time.text =  getTime(item.form.endTime)
            trailer_begin.text =  (item.form.trailerBeginReading.toString())
            trailer_end.text =  (item.form.trailerEndReading.toString())

            if (item.destination.wayPointTypeDescription != "Source") {
                stick_reading_before.text =  ((item.form.stickReadingBefore?:"N/A") as CharSequence?)
                stick_reading_after.text =  ((item.form.stickReadingAfter?:"N/A") as CharSequence?)
                meter_reading_before.text =  ((item.form.meterReadingBefore?:"N/A") as CharSequence?)
                meter_reading_after.text =  ((item.form.meterReadingAfter?:"N/A") as CharSequence?)
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

    /**
     * Get image path
     * This method gets the list of image path.
     * @param list list of image path
     * @return list of image path
     */
    private fun getImagePath(list: List<BillOfLadingImages>): List<String> {
        return list.map {
            it.imagePath
        }
    }

    /**
     * Add bold with text
     * THis method add bold text
     * @param view view where text is to be added
     * @param boldText text to be added
     * @param text data to be added
     */
    private fun addBoldWithText(view: TextView, boldText: String, text: String) {
        val notesText = "<b> $boldText </b> $text"
        view.text = htmlToText(notesText)
    }
}



