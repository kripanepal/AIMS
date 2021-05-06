package com.fourofourfound.aims_delivery.homePage

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.database.utilClasses.FuelWithInfo
import com.fourofourfound.aimsdelivery.R

/**
 * Fuel summary adapter
 * This adapter provides access to the fuel summary and creates a view for
 * each item in the data set.
 * @property dataSet array of fuel information
 * @constructor Create empty Fuel summary adapter
 */
class FuelSummaryAdapter(private val dataSet: Array<FuelWithInfo>) :
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

    /**
     * On create view holder
     * Called when RecyclerView needs a new view holder of the given type to represent
     * an item.
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fuel_summary_view, viewGroup, false)
        return ViewHolder(view)
    }

    /**
     * This method displays an item on the view holder.
     * @param viewHolder holds the trip in the list
     * @param position the position of the trip in the list
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.fuelType.text = dataSet[position].fuel_type
        viewHolder.fuelSource.text = dataSet[position].fuel_source
        viewHolder.siteCount.text = dataSet[position].site_count
        if (position == 0) {
            viewHolder.fuelType.setTypeface(null, Typeface.BOLD)
            viewHolder.fuelSource.setTypeface(null, Typeface.BOLD)
            viewHolder.siteCount.setTypeface(null, Typeface.BOLD)
        }
    }

    /**
     * Get item count
     * Returns the size of the data set.
     */
    override fun getItemCount() = dataSet.size
}


