package com.example.a207424_encikizwanbinazmi_lab1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class SecondLifeDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: SecondLifeDatabase? = null

        fun getDatabase(context: Context): SecondLifeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecondLifeDatabase::class.java,
                    "secondlife_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}