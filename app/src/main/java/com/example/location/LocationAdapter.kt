package com.example.location

import android.R
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TableRow.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.location.databinding.CustomLocationRecordsBinding
import kotlinx.android.synthetic.main.activity_main.*


class LocationAdapter : ListAdapter<LocationRecords,
        LocationAdapter.ViewHolder>(TripsDiffCallBack()) {

    var data = listOf<LocationRecords>()

    //how to create the view holder
    //viewGroup is  always the recycler view in this case
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    //what data to display on the view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = getItem(position)

        holder.bind(item)
        holder.bind(getItem(position)!!)
    }


    class ViewHolder private constructor(var binding: CustomLocationRecordsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //access how to inflate the view using static methods
        companion object {
            lateinit var context: Context
            fun from(parent: ViewGroup): ViewHolder {
                //create a new layout from the parent context
                var layoutInflater = LayoutInflater.from(parent.context)
                context = parent.context

                //creating a binding
                val binding = CustomLocationRecordsBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }

        //change view
        fun bind(item: LocationRecords) {
            //added a new binding
            binding.locationRecord = item
            binding.executePendingBindings()
        }
    }

    //using diffUtill to change the list
    class TripsDiffCallBack : DiffUtil.ItemCallback<LocationRecords>() {
        override fun areItemsTheSame(oldItem: LocationRecords, newItem: LocationRecords): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(
            oldItem: LocationRecords,
            newItem: LocationRecords
        ): Boolean {
            return oldItem == newItem
        }
    }
}

