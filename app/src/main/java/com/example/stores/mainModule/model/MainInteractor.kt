package com.example.stores.mainModule.model

import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.common.utils.Constants.ERROR
import com.example.stores.common.utils.Constants.GET_ALL_PATH
import com.example.stores.common.utils.Constants.STATUS_PROPERTY
import com.example.stores.common.utils.Constants.STORES_URL
import com.example.stores.common.utils.Constants.SUCCES
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.invoke.MutableCallSite

class MainInteractor {

    fun getStores(callback: (MutableList<StoreEntity>) -> Unit) {

        var storeList = mutableListOf<StoreEntity>()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, STORES_URL+ GET_ALL_PATH, null, { response ->

            val status =response.optInt(STATUS_PROPERTY, ERROR)

            if (status== SUCCES){
//
                val jsonList =  response.optJSONArray(Constants.STORES_PROPERTY)?.toString()
                if (jsonList!=null) {
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>() {}.type
                     storeList = Gson().fromJson(jsonList, mutableListType)

                    callback(storeList)
                    return@JsonObjectRequest
                }
            }
            callback(storeList)
        }, {
            it.printStackTrace()
        })

        StoreApplication.storeApi.addToRequestQueue(jsonObjectRequest)

    }

    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit) {
        doAsync {
            val storesList = StoreApplication.database.storeDao().getAllstore()
            uiThread {
                val json = Gson().toJson(storesList)
                Log.i("Gson", json)
                callback(storesList)
            }
        }
    }

    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit) {
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {

                callback(storeEntity)
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit) {
        doAsync {
            StoreApplication.database.storeDao().UpdateStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }
}

