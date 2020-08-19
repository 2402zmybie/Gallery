package com.hr.gallery

import android.icu.lang.UCharacter
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hr.gallery.adapter.GalleryAdapter
import com.hr.gallery.viewmodel.GalleryViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment:Fragment() {
    //简易viewmodel的写法
    private val galleryViewModel by viewModels<GalleryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //1 显示menu
        setHasOptionsMenu(true)
        var galleryAdapter = GalleryAdapter()
        recyclerView.apply {
            adapter = galleryAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        }

        //下拉刷新监听
        swipeLayoutGallery.setOnRefreshListener {
            galleryViewModel.resetQuery()
        }

        galleryViewModel.pageListLiveData.observe(viewLifecycleOwner, Observer {
            galleryAdapter.submitList(it)
            swipeLayoutGallery.isRefreshing = false
        })


    }


    //2 显示menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.swiperRefreshCaditor -> {
                swipeLayoutGallery.isRefreshing = true
                galleryViewModel.resetQuery()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}