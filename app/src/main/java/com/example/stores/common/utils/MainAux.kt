package com.example.stores.common.utils

import com.example.stores.common.entities.StoreEntity

interface MainAux {

    fun hideFab(isVisible :Boolean = false)

    fun addStorage(storeEntity: StoreEntity)

    fun updateStorage(storeEntity: StoreEntity)
}