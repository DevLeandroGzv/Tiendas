package com.example.stores

interface MainAux {

    fun hideFab(isVisible :Boolean = false)

    fun addStorage(storeEntity: StoreEntity)

    fun updateStorage(storeEntity: StoreEntity)
}