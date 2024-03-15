package com.example.stores.mainModule.model

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.invoke.MutableCallSite

class MainInteractor {


    fun getStores(callback: (MutableList<StoreEntity>) -> Unit) {
        doAsync {
            val storesList = StoreApplication.database.storeDao().getAllstore()
            uiThread {
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

