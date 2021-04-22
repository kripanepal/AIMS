package com.fourofourfound.aims_delivery.deliveryForms.finalForm

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aimsdelivery.databinding.LinearGalleryBinding

class BillOfLadingAdapter(
    var clickListener: BitmapListListener
) : ListAdapter<Bitmap,
        BillOfLadingAdapter.ViewHolder>(BitmapDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = getItem(position)
        holder.bind(item, clickListener)
        holder.bind(getItem(position)!!, clickListener)
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

        fun bind(item: Bitmap, clickListener: BitmapListListener) {
            binding.clickListener = clickListener
            binding.bitmap = item
            binding.billOfLadingImage.setImageBitmap(item)

            binding.executePendingBindings()
        }
    }

    class BitmapDiffCallBack : DiffUtil.ItemCallback<Bitmap>() {

        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            //check for similar item
            return oldItem.sameAs(newItem)
        }

        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            //check for same item
            return oldItem.sameAs(newItem)
        }
    }
}

class BitmapListListener(
    val deleteListener: (bitmap: Bitmap) -> Unit,
    val enlargeListener: (bitmap: Bitmap) -> Unit
) {
    fun delete(bitmap: Bitmap) {
        deleteListener(bitmap)
    }

    fun enlarge(bitmap: Bitmap) {
        enlargeListener(bitmap)
    }
}