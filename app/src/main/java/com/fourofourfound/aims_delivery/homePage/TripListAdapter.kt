package com.fourofourfound.aims_delivery.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aimsdelivery.databinding.TripListListViewBinding


class TripListAdapter(private val clickListener: TripListListener) : ListAdapter<Trip,
        TripListAdapter.ViewHolder>(TripsDiffCallBack()) {


    var data = listOf<Trip>()

    //how to create the view holder
    //viewGroup is  always the recycler view in this case
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    //what data to display on the view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = getItem(position)

        holder.bind(item, clickListener)
        holder.bind(getItem(position)!!, clickListener)
    }


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

        //change view
        fun bind(item: Trip, clickListener: TripListListener) {
            //added a new binding
            binding.trip = item
            binding.clickListener = clickListener

            //makes the nested view expandable
            binding.cardView.setOnClickListener{
                binding.cardViewNestedView.apply {
                    visibility = if(visibility=== View.VISIBLE) View.GONE
                    else View.VISIBLE
                }
            }

            binding.executePendingBindings()
        }
    }

    //using diffUtill to change the list
    class TripsDiffCallBack : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem == newItem
        }
    }
}

class TripListListener(val clickListener: (trip: Trip) -> Unit) {
    fun onClick(trip: Trip) {
        clickListener(trip)
    }
}