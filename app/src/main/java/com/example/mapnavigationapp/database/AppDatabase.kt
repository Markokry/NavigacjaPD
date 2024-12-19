package com.example.mapnavigationapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapnavigationapp.entity.MarkerEntity
import com.example.mapnavigationapp.entity.RouteEntity

@Database(entities = [MarkerEntity::class, RouteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mapDao(): MapDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "map_navigation.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}