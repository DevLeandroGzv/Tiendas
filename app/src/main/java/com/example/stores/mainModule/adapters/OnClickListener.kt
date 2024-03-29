package com.example.stores.mainModule.adapters

import com.example.stores.common.entities.StoreEntity

interface OnClickListener {

    fun onClick(storeEntity: StoreEntity)

    fun onFavoriteStore(storeEntity: StoreEntity)

    fun onDeleteStore(storeEntity: StoreEntity)
}