package com.fourofourfound.aims_delivery.homePage

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.database.utilClasses.Fuel_with_info
import com.fourofourfound.aimsdelivery.R

class FuelSummaryAdapter(private val dataSet: Array<Fuel_with_info>) :
    RecyclerView.Adapter<FuelSummaryAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fuelType: TextView = view.findViewById(R.id.fuel_type)
        val fuelSource: TextView = view.findViewById(R.id.fuel_source)
        val siteCount: TextView = view.findViewById(R.id.site_count)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fuel_summary_view, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.fuelType.text = dataSet[position].fuel_type
        viewHolder.fuelSource.text = dataSet[position].fuel_source
        viewHolder.siteCount.text = dataSet[position].site_count

        if (position == 0) {
            viewHolder.fuelType.setTypeface(null, Typeface.BOLD)
            viewHolder.fuelSource.setTypeface(null, Typeface.BOLD)
            viewHolder.siteCount.setTypeface(null, Typeface.BOLD)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}


