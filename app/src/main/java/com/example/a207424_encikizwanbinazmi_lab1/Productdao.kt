package com.example.a207424_encikizwanbinazmi_lab1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: ProductEntity)

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAll(): Flow<List<ProductEntity>>

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("SELECT COUNT(*) FROM products")
    fun getCount(): Flow<Int>
}