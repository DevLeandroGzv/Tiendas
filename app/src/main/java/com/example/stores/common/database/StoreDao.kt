package com.example.stores.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.stores.common.entities.StoreEntity


@Dao
interface StoreDao {

    @Query("SELECT * FROM StoreEntity")
    fun getAllstore(): MutableList<StoreEntity>

    @Insert
    fun addStore(storeEntity: StoreEntity): Long

    @Update
    fun UpdateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)

    @Query("SELECT * FROM storeentity WHERE id = :id")
    fun getStoreByID(id : Long) : StoreEntity
}