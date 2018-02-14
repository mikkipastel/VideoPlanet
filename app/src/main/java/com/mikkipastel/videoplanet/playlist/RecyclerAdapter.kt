package com.mikkipastel.videoplanet.playlist

import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikkipastel.videoplanet.R
import android.support.v7.widget.RecyclerView


class RecyclerAdapter(internal var mListener: ItemListener, internal var mVideoList: Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var mItemCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // set cover and track name
        val mHolder = holder as ViewHolder
        mHolder.name.text = mVideoList[position]
        mHolder.itemView.setOnClickListener { v -> mListener.onClick(v, position) }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var name: TextView = view.findViewById(R.id.txt_name) as TextView

    }

    override fun getItemCount(): Int {
        var itemCount = mItemCount
        if (itemCount == 0) {
            itemCount = mVideoList.size
        }
        return itemCount
    }

}
