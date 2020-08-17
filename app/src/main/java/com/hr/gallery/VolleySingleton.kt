package com.hr.gallery

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

//私有构造的单例
class VolleySingleton private constructor(val context: Context) {

    companion object {

        private var INSTANCE : VolleySingleton? = null
        fun getInstance(context: Context): VolleySingleton? {
            if(INSTANCE == null) {
                VolleySingleton(context).also { INSTANCE = it }
                return INSTANCE
            }
            return INSTANCE
        }
    }

    val requestQueue:RequestQueue by lazy { Volley.newRequestQueue(context) }


}