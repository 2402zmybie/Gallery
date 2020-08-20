package com.hr.gallery.adapter

import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hr.gallery.PagerPhotoActivity
import com.hr.gallery.PhotoItem
import com.hr.gallery.R
import com.hr.gallery.paging.NetworkStatus
import com.hr.gallery.viewmodel.GalleryViewModel
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*
import org.jetbrains.anko.startActivity
import kotlin.collections.ArrayList

class GalleryAdapter(private val galleryViewModel: GalleryViewModel) : PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFFCALLBACK) {
    private var networkStatus: NetworkStatus? = null
    private var hasFooter = false


    fun updateNetWorkStatus(networkStatus: NetworkStatus?) {
        this.networkStatus = networkStatus
        if (networkStatus == NetworkStatus.INITIAL_LOADING) hideFooter() else showFooter()
    }

    private fun hideFooter() {
        if(hasFooter) {
            notifyItemRemoved(itemCount - 1)
        }
        hasFooter = false
    }

    private fun showFooter() {
        if(hasFooter) {
            notifyItemChanged(itemCount - 1)
        }else {
            hasFooter = true
            notifyItemInserted(itemCount - 1)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount - 1) R.layout.gallery_footer else R.layout.gallery_cell
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.gallery_cell -> {
                PhotoViewHolder.newInstance(parent).also {holder->
                    holder.itemView.setOnClickListener {
                        //跳转到viwepager2画廊界面
                        var bundle: Bundle = Bundle()
                        bundle.putParcelableArrayList("PHOTO_LIST", ArrayList<PhotoItem>(currentList!!))
                        bundle.putInt("PHOTO_POSITION", holder.adapterPosition)
                        parent.context.startActivity<PagerPhotoActivity>("PHOTO_DATA" to bundle)
                    }
                }
            }
            else -> {
                FooterViewHolder.newInstance(parent).also {
                    it.itemView.setOnClickListener {
                        galleryViewModel.retry()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when(holder.itemViewType) {
           R.layout.gallery_footer -> (holder as FooterViewHolder).bindWithNetwordStatus(networkStatus)
           else -> {
               var photoItem = getItem(position) ?: return
               (holder as PhotoViewHolder).bindWithPhotoItem(photoItem)
           }
       }
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }

    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun newInstance(parent: ViewGroup): PhotoViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.gallery_cell, parent, false)
                return PhotoViewHolder(view)
            }
        }

        fun bindWithPhotoItem(pictureItem: PhotoItem) {
            with(itemView) {
                this.shimmerLayoutCell.apply {
                    setShimmerColor(0x55FFFFFF)
                    setShimmerAngle(0)
                    //开始闪动
                    startShimmerAnimation()
                }
                this.tv_title.text = pictureItem!!.user
                this.tv_thumb.text = pictureItem.likes.toString()
                this.tv_favorite.text = pictureItem.favorites.toString()

            }

            Glide.with(itemView)
                .load(pictureItem!!.previewURL)
                .placeholder(R.drawable.photo_placeholder)
                .listener(object : RequestListener<Drawable> {
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
                        return false.also { itemView.shimmerLayoutCell?.stopShimmerAnimation() }
                    }
                })
                .into(itemView.imageView)
        }
    }


    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun newInstance(parent: ViewGroup): FooterViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.gallery_footer, parent, false)
                (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                return FooterViewHolder(view)
            }
        }

        fun bindWithNetwordStatus(networkStatus: NetworkStatus?) {
            with(itemView) {
                when (networkStatus) {
                    NetworkStatus.FAILED -> {
                        textView.text = "点击重试"
                        progressBar.visibility = View.GONE
                        isClickable = true
                    }
                    NetworkStatus.COMPLETED -> {
                        textView.text = "加载完毕"
                        progressBar.visibility = View.GONE
                        isClickable = false
                    }
                    else -> {
                        textView.text = "正在加载"
                        progressBar.visibility = View.VISIBLE
                        isClickable = false
                    }
                }
            }
        }
    }

}

