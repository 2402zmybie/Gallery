package com.hr.gallery.paging

import android.content.Context
import androidx.paging.DataSource
import com.hr.gallery.PhotoItem

class PixabayDataSourceFactory(val context: Context): DataSource.Factory<Int, PhotoItem>() {
    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context)
    }
}