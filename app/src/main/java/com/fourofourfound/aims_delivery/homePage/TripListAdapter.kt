package com.fourofourfound.aims_delivery.homePage


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.database.utilClasses.FuelWithInfo
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.utils.toggleDropDownImage
import com.fourofourfound.aims_delivery.utils.toggleViewVisibility
import com.fourofourfound.aimsdelivery.databinding.TripListListViewBinding

/**
 * Trip list adapter
 * This class is the adapter for a recycler view in the homePage that
 * displays the list of trips
 * @property clickListener click handler for a button  for each trip
 * @constructor Create empty Trip list adapter
 */
class TripListAdapter(
    private val clickListener: TripListListener,
) : ListAdapter<Trip,
        TripListAdapter.ViewHolder>(TripsDiffCallBack()) {

    /**
     * On create view holder
     * Called when RecyclerView needs a new view holder of the given type to represent
     * an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * This method displays an item on the view holder
     * @param holder holds the trip in the list
     * @param position the position of the trip in the list
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = getItem(position)
        holder.bind(item, clickListener)
        holder.bind(getItem(position)!!, clickListener)
    }

    /**
     * View holder
     *This is the viewHolder for each trip in the list.
     * All trip lists are displayed using this holder
     * @property binding
     * @constructor Create empty View holder
     */
    class ViewHolder private constructor(var binding: TripListListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //access how to inflate the view using static methods
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //create a new layout from the parent context
                var layoutInflater = LayoutInflater.from(parent.context)
                //creating a binding
                val binding = TripListListViewBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }

        /**
         * Bind
         *This methods sets how each item is to be initialized ans displayed
         * @param item the item being displayed in the recycler view
         * @param clickListener the clickHandler for the item being displayed
         */
        fun bind(item: Trip, clickListener: TripListListener) {
            //add a new binding
            binding.trip = item
            binding.clickListener = clickListener
            //makes the nested view expandable
            binding.cardView.setOnClickListener {
                binding.cardViewNestedView.apply {
                    toggleViewVisibility(this)
                    toggleDropDownImage(this, binding.tripDropDownImage)
                }
            }
            val adapter = FuelSummaryAdapter(calculateFuelInfo(item).toTypedArray())
            binding.nestedTripDetailsListView.adapter = adapter
            binding.executePendingBindings()
        }


        /**
         * Calculate fuel info
         * This method calculates the total type fuel, its source, and number of sites.
         * @param item the product to be be shown
         * @return the list of products
         */
        private fun calculateFuelInfo(item: Trip): MutableList<FuelWithInfo> {
            var fuelInfo = mutableListOf<FuelWithInfo>()
            fuelInfo.add(FuelWithInfo("Fuel Type", "Source", "#Site"))
            //get  lists of all product types for that trip
            val productList = ArrayList<String>(item.sourceOrSite.size)
            for (destination in item.sourceOrSite) if (!productList.contains(destination.productInfo.productDesc!!)) productList.add(
                destination.productInfo.productDesc!!
            )
            for ((index, product) in productList.withIndex()) {
                //find all sources  for each fuel type
                val sourceList = item.sourceOrSite.filter {
                    (it.productInfo.productDesc == product)
                }
                if (sourceList.isNotEmpty()) {
                    var sorted = sourceList.sortedWith(compareBy { it.seqNum })
                        .filter { (it.wayPointTypeDescription == "Source") }
                    val numberOfSites = sourceList.size - sorted.size
                    val productName = productList[index]
                    var sourceName =
                        if (sorted.isEmpty()) "--" else sorted[0].location.destinationName
                    var fuelWithInfo =
                        FuelWithInfo(productName!!, sourceName, numberOfSites.toString())
                    fuelInfo.add(fuelWithInfo)
                }
            }
            return fuelInfo
        }
    }

    /**
     * Trips diff call back
     * This class expands DiffUtil to make changes to the recycler view
     * @constructor Create empty Trips diff call back
     */
    class TripsDiffCallBack : DiffUtil.ItemCallback<Trip>() {

        /**
         * Are items the same
         * This method checks if the two objects are same.
         * @param oldItem the old item to be compared
         * @param newItem the new item to be compared
         * @return true if same, false otherwise
         */
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.tripId == newItem.tripId
        }

        /**
         * Are contents the same
         * Called to checks if the content on two objects are same
         * @param oldItem content of the old item
         * @param newItem content of the new item
         * @return true if content are same, false otherwise
         */
        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Trip list listener
 *ClickListener class for each trip
 * @property clickListener lambda expression to define the clickHandler
 * @constructor Create empty Trip list listener
 */
class TripListListener(val clickListener: (trip: Trip) -> Unit) {
    fun onClick(trip: Trip) {
        clickListener(trip)
    }
}