package com.fourofourfound.aims_delivery.deliveryForms.finalForm

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.utils.getBitMapFromFilePath
import com.fourofourfound.aimsdelivery.databinding.LinearGalleryBinding

class BillOfLadingAdapter(
    var clickListener: BitmapListListener,
    var parentContext: Context
) : ListAdapter<String,
        BillOfLadingAdapter.ViewHolder>(ViewHolder.BitmapDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //var item = getItem(position)
        //holder.bind(item, clickListener, parentContext)
        holder.bind(getItem(position)!!, clickListener, parentContext)
    }

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

        fun bind(item: String, clickListener: BitmapListListener, parentContext: Context) {
            binding.clickListener = clickListener
            if (item.isNotBlank()) {

                val bitmap = getBitMapFromFilePath(parentContext, item)
                binding.billOfLadingImage.setImageBitmap(bitmap)
                binding.imagePath = item
                binding.bitmap = bitmap
            }
            binding.executePendingBindings()
        }


        class BitmapDiffCallBack : DiffUtil.ItemCallback<String>() {

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                //check for similar item
                return oldItem == (newItem)
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                //check for same item
                return oldItem == (newItem)
            }
        }
    }
}

class BitmapListListener(
    val deleteListener: (imageUri: String) -> Unit,
    val enlargeListener: (imageBitmap: Bitmap) -> Unit
) {
    fun delete(imageUri: String) {
        deleteListener(imageUri)
    }

    fun enlarge(imageBitmap: Bitmap) {
        enlargeListener(imageBitmap)
    }
}
