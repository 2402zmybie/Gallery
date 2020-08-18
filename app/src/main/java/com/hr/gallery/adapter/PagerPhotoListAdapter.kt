package com.hr.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hr.gallery.PhotoItem
import com.hr.gallery.R
import kotlinx.android.synthetic.main.pager_photo_view.view.*

class PagerPhotoListAdapter: ListAdapter<PhotoItem,PagerPhotoListAdapter.MyViewHolder>(object :DiffUtil.ItemCallback<PhotoItem>(){
    override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem.id == newItem.id
    }
}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view,parent,false).apply {
            return MyViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(getItem(position).previewURL)
            .placeholder(R.drawable.photo_placeholder)
            .into(holder.itemView.pagerPhoto)

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}