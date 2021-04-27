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
 *This class is the adapter for a recycler view in the homePage that
 * displays the list of trips
 * @property clickListener click handler for a button  for each trip
 * @constructor Create empty Trip list adapter
 */
class TripListAdapter(
    private val clickListener: TripListListener,
) : ListAdapter<Trip,
        TripListAdapter.ViewHolder>(TripsDiffCallBack()) {


    /**
    create the view holder
    viewGroup is  always the recycler view in this case
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    /**
     * This method displays an item on the view holder
     *
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
            var fuelInfo = mutableListOf<FuelWithInfo>()
            fuelInfo.add(FuelWithInfo("Fuel Type", "Source", "#Site"))

            //get  lists of all product types for that trip
            val productList = HashSet<Int>(item.sourceOrSite.size)
            for (destination in item.sourceOrSite) productList.add(destination.productInfo.productId!!)

            if (productList != null) {
                for (product in productList) {
                    //find all sources  for each fuel type
                    val sourceList = item.sourceOrSite.filter {
                        (it.wayPointTypeDescription == "Source") && (it.productInfo.productId == product)
                    }

                    if (sourceList.isNotEmpty()) {
                        //order the source by seq number
                        var firstElement = sourceList.sortedWith(compareBy { it.seqNum })[0]
                        val numberOfSites = item.sourceOrSite.size - sourceList.size
                        val productName = firstElement.productInfo.productDesc
                        var sourceName =
                            if (firstElement.wayPointTypeDescription == "Source") firstElement.location.destinationName else "Not Available"
                        var fuelWithInfo =
                            FuelWithInfo(productName!!, sourceName, numberOfSites.toString())
                        fuelInfo.add(fuelWithInfo)
                    }
                }

            }
            //makes the nested view expandable
            binding.cardView.setOnClickListener {
                binding.cardViewNestedView.apply {
                    toggleViewVisibility(this)
                    toggleDropDownImage(this,binding.tripDropDownImage)
                }
            }


            val adapter = FuelSummaryAdapter(fuelInfo.toTypedArray())
            binding.nestedTripDetailsListView.adapter = adapter
            binding.executePendingBindings()
        }
    }

    /**
     * Trips diff call back
     *This class expands DiffUtill to make changes to the recycler view
     *
     * @constructor Create empty Trips diff call back
     */
    class TripsDiffCallBack : DiffUtil.ItemCallback<Trip>() {


        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            //check for similar item
            return oldItem.tripId == newItem.tripId
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            //check for same item
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