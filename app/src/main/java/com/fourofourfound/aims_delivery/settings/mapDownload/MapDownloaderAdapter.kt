package com.fourofourfound.aims_delivery.settings.mapDownload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aimsdelivery.databinding.StateListViewBinding

class MapDownloaderAdapter(private val clickListener: StateClickHandler) : ListAdapter<String,
        MapDownloaderAdapter.ViewHolder>(StateDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = getItem(position)
        holder.bind(item, clickListener)
        holder.bind(getItem(position)!!, clickListener)
    }

    class ViewHolder private constructor(var binding: StateListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                var layoutInflater = LayoutInflater.from(parent.context)
                val binding = StateListViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }


        fun bind(item: String, clickListener: StateClickHandler) {
            binding.state = item
            binding.clickHandler = clickListener
            binding.executePendingBindings()
        }
    }

    class StateDiffCallBack : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}


class StateClickHandler(val clickListener: (state: String) -> Unit) {
    fun onClick(state: String) {
        clickListener(state)
    }
}