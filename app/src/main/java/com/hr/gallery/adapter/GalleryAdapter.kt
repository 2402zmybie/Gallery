package com.hr.gallery.adapter

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hr.gallery.PagerPhotoActivity
import com.hr.gallery.PhotoActivity
import com.hr.gallery.PhotoItem
import com.hr.gallery.R
import kotlinx.android.synthetic.main.gallery_cell.view.*
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GalleryAdapter : ListAdapter<PhotoItem, GalleryAdapter.MyViewHolder>(DIFFCALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var holder = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell,parent,false))
        holder.itemView.setOnClickListener {
//            Bundle().apply {
//                this.putParcelable("PHOTO",)
//            }
            //跳转到显示图片界面
//           parent.context.startActivity<PhotoActivity>("PHOTO" to getItem(holder.adapterPosition))
            //跳转到viwepager2画廊界面
            var bundle:Bundle = Bundle()
            bundle.putParcelableArrayList("PHOTO_LIST", ArrayList<PhotoItem>(currentList))
            bundle.putInt("PHOTO_POSITION", holder.adapterPosition)
            parent.context.startActivity<PagerPhotoActivity>("PHOTO_DATA" to bundle)
        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.shimmerLayoutCell.apply {
            //几件事一起做(或者用run)
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            //开始闪动
            startShimmerAnimation()
        }
        Glide.with(holder.itemView)
            .load(getItem(position).previewURL)
            .placeholder(R.drawable.photo_placeholder)
            .listener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { holder.itemView.shimmerLayoutCell?.stopShimmerAnimation() }
                }


            })
            .into(holder.itemView.imageView)

    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

}

