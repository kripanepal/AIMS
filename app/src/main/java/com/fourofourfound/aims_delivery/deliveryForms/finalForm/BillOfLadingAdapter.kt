package com.fourofourfound.aims_delivery.deliveryForms.finalForm

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.utils.getBitMapFromFilePath
import com.fourofourfound.aimsdelivery.databinding.LinearGalleryBinding

/**
 * Bill of lading adapter
 * This class is responsible for showing the list of images in the recycler view.
 * @property clickListener the click listener for the image
 * @property parentContext the parent context
 * @constructor Create empty Bill of lading adapter
 */
class BillOfLadingAdapter(
    var clickListener: BitmapListListener,
    var parentContext: Context
) : ListAdapter<String,
        BillOfLadingAdapter.ViewHolder>(ViewHolder.BitmapDiffCallBack()) {

    /**
     * On create view holder
     * Called when the view holder is created.
     * @param parent the parent view group
     * @param viewType the type of the view
     * @return the new view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * On bind view holder
     * Called by RecyclerView to display the data at the specified position.
     * @param holder the view holder
     * @param position the position of the view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener, parentContext)
    }

    /**
     * View holder
     * This class represents how each data is represented in the view holder.
     * @property binding the liner gallery binding object
     * @constructor Create empty View holder
     */
    class ViewHolder private constructor(var binding: LinearGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //access how to inflate the view using static methods
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //create a new layout from the parent context
                var layoutInflater = LayoutInflater.from(parent.context)
                //creating a binding
                val binding = LinearGalleryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        /**
         * Bind
         * This method binds the view with the data
         * @param item the data to be shown
         * @param clickListener the view click listener
         * @param parentContext the parent context
         */
        fun bind(item: String, clickListener: BitmapListListener, parentContext: Context) {
            binding.clickListener = clickListener
            if (item.isNotBlank()) {
                val bitmap = getBitMapFromFilePath(parentContext, item)
                binding.billOfLadingImage.setImageBitmap(bitmap)
                binding.imagePath = item
                binding.bitmap = bitmap
                if (clickListener.deleteListener == null) {
                    binding.closeButton.visibility = View.GONE
                }
            }
            binding.executePendingBindings()
        }

        /**
         * Bitmap diff call back
         * This class expands DiffUtil to make changes to the recycler view.
         * @constructor Create empty Bitmap diff call back
         */
        class BitmapDiffCallBack : DiffUtil.ItemCallback<String>() {

            /**
             * Are items the same
             * Called to check if two items are same
             * @param oldItem old item to be compared
             * @param newItem new item to be compared
             * @return true if items are same, false otherwise
             */
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                //check for similar item
                return oldItem == (newItem)
            }

            /**
             * Are contents the same
             * Called to checks if the content on two objects are same
             * @param oldItem content of the old item
             * @param newItem content of the new item
             * @return true if content are same, false otherwise
             */
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == (newItem)
            }
        }
    }
}

/**
 * Bitmap list listener
 * This class is responsible for image to delete and enlarge the image.
 * @property deleteListener called when delete is click
 * @property enlargeListener called when image is clicked
 * @constructor Create empty Bitmap list listener
 */
class BitmapListListener(
    val deleteListener: ((imageUri: String) -> Unit)? = null,
    val enlargeListener: (imageBitmap: Bitmap) -> Unit
) {
    /**
     * Delete
     * This method deletes the image.
     * @param imageUri the file path of the image
     */
    fun delete(imageUri: String) {
        if (deleteListener != null) {
            deleteListener!!(imageUri)
        }
    }

    /**
     * Enlarge
     * This method expands the image
     * @param imageBitmap bitmap of the image
     */
    fun enlarge(imageBitmap: Bitmap) {
        enlargeListener(imageBitmap)
    }
}
