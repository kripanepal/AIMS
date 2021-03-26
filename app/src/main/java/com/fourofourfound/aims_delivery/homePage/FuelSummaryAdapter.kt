package com.fourofourfound.aims_delivery.homePage

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
        val fuelType: TextView
        val fuelSource: TextView
        val siteCount: TextView

        init {
            // Define click listener for the ViewHolder's View.
            fuelType = view.findViewById(R.id.fuel_type)
            fuelSource = view.findViewById(R.id.fuel_source)
            siteCount = view.findViewById(R.id.site_count)
        }
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}


