package com.example.clocker

import Entity.Clock
import Interface.OnClockItemClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var txtPersonId: TextView = view.findViewById(R.id.txtPersonIdItem_recycler)
    var txtDate: TextView = view.findViewById(R.id.txtDateItem_recycler)
    var txtType: TextView = view.findViewById(R.id.txtTypeItem_recycler)
    var imgPhoto: ImageView = view.findViewById(R.id.imgPhotoClock_ItemRecycler)

    fun bind(item: Clock, clickListener: OnClockItemClickListener) {
        txtPersonId.text = item.IDPerson
        txtDate.text = item.DateClock.toString()
        txtType.text = item.Type
        imgPhoto.setImageBitmap(item.Photo)

        itemView.setOnClickListener {
            clickListener.onClockItemClicked(item)
        }
    }
}

class ClockListAdapter(
    private var itemList: List<Clock>,
    private val itemClickListener: OnClockItemClickListener
) : RecyclerView.Adapter<ClockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClockViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_clock, parent, false)
        return ClockViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClockViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, itemClickListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
