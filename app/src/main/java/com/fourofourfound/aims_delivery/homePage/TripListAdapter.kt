package com.fourofourfound.aims_delivery.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aimsdelivery.databinding.TripListListViewBinding

/**
 * Trip list adapter
 *This class is the adapter for a recycler view in the homePage that
 * displays the list of trips
 * @property clickListener click handler for a button  for each trip
 * @constructor Create empty Trip list adapter
 */
class TripListAdapter(private val clickListener: TripListListener) : ListAdapter<Trip,
        TripListAdapter.ViewHolder>(TripsDiffCallBack()) {

    /**
     * Data the list of trips that is used by the recycler view
     */
    var data = listOf<Trip>()


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

            //added a new binding
            binding.trip = item
            binding.clickListener = clickListener

            //makes the nested view expandable
            binding.cardView.setOnClickListener {
                binding.cardViewNestedView.apply {
                    visibility = if (visibility === View.VISIBLE) View.GONE
                    else View.VISIBLE
                }
            }

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
            return oldItem.tripID == newItem.travelID
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