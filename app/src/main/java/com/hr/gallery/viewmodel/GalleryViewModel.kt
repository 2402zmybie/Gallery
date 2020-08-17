package com.hr.gallery.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.hr.gallery.PhotoItem
import com.hr.gallery.Pixabay
import com.hr.gallery.VolleySingleton

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    //私有LiveData数据源
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    //给外界提供获取数据源的方法
    public val photoListLive
    get() = _photoListLive

    //获取URL
    private val keyWords = arrayOf("cat","dog","car")
    private fun getUrl():String {
        return "https://pixabay.com/api/?key=17921301-974ad23d82135fa91669f2b9f&per_page=100&q=${keyWords.random()}"
    }

    //定义获取数据的方法 (实际应该放在仓库类中, 简化则放在ViewModel中)
    fun fetchData() {
        var stringRequest = StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                var photoItems = Gson().fromJson(it, Pixabay::class.java).hits.toList()
                _photoListLive.value = photoItems

            },
            Response.ErrorListener {
                Log.e("TAG","请求出错")
            }
        )
        //加入请求队列
        VolleySingleton.getInstance(getApplication())?.requestQueue?.add(stringRequest)
    }

}