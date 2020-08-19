package com.hr.gallery.paging

import android.content.Context
import androidx.paging.PageKeyedDataSource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.hr.gallery.PhotoItem
import com.hr.gallery.Pixabay
import com.hr.gallery.VolleySingleton

//网络数据分页加载 PageKeyedDataSource
class PixabayDataSource(private val context:Context): PageKeyedDataSource<Int, PhotoItem>() {

    val keyWord = arrayOf("cat","dog","car","flowers","beauty","animal").random()

    //初始化
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
       val url = "https://pixabay.com/api/?key=17921301-974ad23d82135fa91669f2b9f&q=${keyWord}&per_page=20&page=1"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                var dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                callback.onResult(dataList,null,2)
            },
            Response.ErrorListener {
                LogUtils.e("加载图片失败")
            }
        ).also {
            VolleySingleton.getInstance(context)?.requestQueue?.add(it) }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        val url = "https://pixabay.com/api/?key=17921301-974ad23d82135fa91669f2b9f&q=${keyWord}&per_page=20&page=${params.key}"
        StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                var dataList = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                //数据;  下一页的序号
                callback.onResult(dataList,params.key + 1)
            },
            Response.ErrorListener {
                LogUtils.e("加载图片失败")
            }
        ).also { VolleySingleton.getInstance(context)?.requestQueue?.add(it) }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {

    }
}