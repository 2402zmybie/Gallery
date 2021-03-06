package com.hr.gallery.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.hr.gallery.paging.PixabayDataSourceFactory

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

//    val pageListLiveData = LivePagedListBuilder(PixabayDataSourceFactory(application),
//        PagedList.Config.Builder()
//        .setPageSize(10)
//        .setEnablePlaceholders(false)
//        .setInitialLoadSizeHint(20)
//        .build()).build()

    private val factory = PixabayDataSourceFactory(application)

    val pageListLiveData = LivePagedListBuilder(factory,1).build()
    //无效化, 重置数据
    fun resetQuery() {
        pageListLiveData.value?.dataSource?.invalidate()
    }

    //得到NetWorkStatus
    val networkStatus = Transformations.switchMap(factory.pixabayDataSource, {
        it.networkStatus
    })

    //重新请求
    fun retry() {
        factory.pixabayDataSource.value?.retry?.invoke()
    }
}