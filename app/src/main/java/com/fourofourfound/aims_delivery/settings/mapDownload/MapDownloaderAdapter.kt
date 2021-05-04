package com.fourofourfound.aims_delivery.settings.mapDownload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aimsdelivery.databinding.MapDownloadEachItemBinding
import com.here.android.mpa.odml.MapPackage

/**
 * Map downloader adapter
 * This adapter provides access to the map data items and creates a view for
 * each item in the data set.
 * @property clickListener the click listener that is trigerred when the item in recycler view is clicked
 * @constructor
 */
class MapDownloaderAdapter(private val clickListener: CurrentItemClickHandler) :
    ListAdapter<MapPackage, MapDownloaderAdapter.ViewHolder>(CurrentItemDiffCallBack()) {

    /**
     * On create view holder
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     * @param parent the parent view group
     * @param viewType the view type of the recycler view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    /**
     * On bind view holder
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    /**
     * View holder
     * The class that defines how each item is structured in the recycler view
     * @property binding the view that is displayed by each item
     * @constructor
     */
    class ViewHolder private constructor(var binding: MapDownloadEachItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            /**
             * From
             * This method inflates the view for each item in the list
             * @param parent the parent view group
             * @return the inflated layout
             */
            fun from(parent: ViewGroup): ViewHolder {
                var layoutInflater = LayoutInflater.from(parent.context)
                val binding = MapDownloadEachItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        /**
         * Bind
         * This methods provides data to the view
         * @param item the map package represented in the view
         * @param clickListener the listener which is trigger after the click on each item
         */
        fun bind(item: MapPackage, clickListener: CurrentItemClickHandler) {
            binding.currentItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    /**
     * Current item diff call back
     * This class calculates the diff between two non-null items in a list.
     * @constructor
     */
    class CurrentItemDiffCallBack : DiffUtil.ItemCallback<MapPackage>() {

        /**
         * Are items the same
         * This method checks if the two objects are same
         * @param oldItem the old item to be compared
         * @param newItem the new item to be compared
         */
        override fun areItemsTheSame(oldItem: MapPackage, newItem: MapPackage) =
            oldItem.id == newItem.id

        /**
         * Are contents the same
         * This method checks if the content on two objects are same
         * @param oldItem content of the old item
         * @param newItem content of the new item
         */
        override fun areContentsTheSame(oldItem: MapPackage, newItem: MapPackage) =
            (oldItem.englishTitle == newItem.englishTitle)
                    && (oldItem.installationState == newItem.installationState)
    }
}

/**
 * Current item click handler
 * This class is responsible for handling the click in map package
 * @property clickListener
 * @constructor
 */
class CurrentItemClickHandler(val clickListener: (currentItem: MapPackage) -> Unit) {
    fun onClick(currentItem: MapPackage) {
        clickListener(currentItem)
    }
}