package com.example.stores.common.database

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class StoreAPI constructor(contex: Context) {

    companion object{
        @Volatile
        private var INSTANCE :StoreAPI? = null

        fun getInstance(contex: Context) = INSTANCE?: synchronized(this) {
            INSTANCE ?: StoreAPI(contex).also {INSTANCE = it}
        }

    }
    val requestQueue :RequestQueue by lazy {
    Volley.newRequestQueue(contex.applicationContext)
    }

    fun <T> addToRequestQueue(req :Request<T>){
        requestQueue.add(req)
    }

}