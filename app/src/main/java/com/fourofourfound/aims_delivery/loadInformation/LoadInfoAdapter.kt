package com.fourofourfound.aims_delivery.loadInformation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.fourofourfound.aims_delivery.database.relations.SourceWithLocationAndFuel
import com.fourofourfound.aimsdelivery.R
import kotlinx.android.synthetic.main.item_view.view.*



class LoadInfoAdapter : RecyclerView.Adapter<MyHolder>() {
    var data = listOf<SourceWithLocationAndFuel>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = data[position]
        val productType = holder.itemView.product_type
        val productQuantity=holder.itemView.product_quantity
        val supplierName=holder.itemView.supplier_name
        val supplierAddress = holder.itemView.supplier_address

    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val productType: TextView = itemView.findViewById(R.id.product_type)
        val productQuantity: TextView = itemView.findViewById(R.id.product_quantity)
        val supplierName : TextView = itemView.findViewById(R.id.supplier_name)
        val supplierAddress : TextView = itemView.findViewById(R.id.supplier_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_view, parent, false) as TextView
        return MyHolder(view)
    }
}