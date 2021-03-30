package com.fourofourfound.aims_delivery.settings.mapDownload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aimsdelivery.databinding.MapDownloadEachItemBinding
import com.here.android.mpa.odml.MapPackage

class MapDownloaderAdapter(private val clickListener: CurrentItemClickHandler) :
    ListAdapter<MapPackage, MapDownloaderAdapter.ViewHolder>(CurrentItemDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(var binding: MapDownloadEachItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                var layoutInflater = LayoutInflater.from(parent.context)
                val binding = MapDownloadEachItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: MapPackage, clickListener: CurrentItemClickHandler) {
            binding.currentItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    class CurrentItemDiffCallBack : DiffUtil.ItemCallback<MapPackage>() {
        override fun areItemsTheSame(oldItem: MapPackage, newItem: MapPackage) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MapPackage, newItem: MapPackage) =
            (oldItem.englishTitle == newItem.englishTitle)
                    && (oldItem.installationState == newItem.installationState)
    }
}

class CurrentItemClickHandler(val clickListener: (currentItem: MapPackage) -> Unit) {
    fun onClick(currentItem: MapPackage) {
        clickListener(currentItem)
    }
}