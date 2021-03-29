package com.fourofourfound.aims_delivery.settings.mapDownload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aimsdelivery.databinding.StateListViewBinding
import com.here.android.mpa.odml.MapPackage

class MapDownloaderAdapter(private val clickListener: StateClickHandler) : ListAdapter<MapPackage,
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


        fun bind(item: MapPackage, clickListener: StateClickHandler) {
            binding.state = item
            binding.clickHandler = clickListener
            binding.executePendingBindings()
        }
    }

    class StateDiffCallBack : DiffUtil.ItemCallback<MapPackage>() {
        override fun areItemsTheSame(oldItem: MapPackage, newItem: MapPackage): Boolean {
            return oldItem.englishTitle == newItem.englishTitle
        }

        override fun areContentsTheSame(oldItem: MapPackage, newItem: MapPackage): Boolean {
            return oldItem.equals(newItem)
        }
    }
}

class StateClickHandler(val clickListener: (state: MapPackage) -> Unit) {
    fun onClick(state: MapPackage) {
        clickListener(state)
    }
}