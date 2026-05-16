package com.metalpizzacat.shoppinglist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Query("select * from product where state=:state")
    fun getAllForState(state: ProductState): Flow<List<Product>>

    @Query("select * from product where state='BOUGHT' and month=:month and year=:year")
    fun getAllBoughtDuring(month: Int, year: Int): Flow<List<Product>>

    @Delete
    suspend fun delete(product : Product)
}