package com.hr.gallery.paging

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hr.gallery.PhotoItem

class PixabayDataSourceFactory(val context: Context): DataSource.Factory<Int, PhotoItem>() {
    private val _pixabayDataSource = MutableLiveData<PixabayDataSource>()
    val pixabayDataSource = _pixabayDataSource
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context).also { _pixabayDataSource.postValue(it) }
    }
}