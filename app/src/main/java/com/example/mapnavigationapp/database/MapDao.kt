package com.example.mapnavigationapp.database

import androidx.room.*
import com.example.mapnavigationapp.model.MarkerEntity
import com.example.mapnavigationapp.model.RouteEntity

@Dao
interface MapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(marker: MarkerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarkers(markers: List<MarkerEntity>)

    @Insert
    suspend fun insertRoute(route: RouteEntity)

    @Update
    suspend fun updateMarker(marker: MarkerEntity)

    @Delete
    suspend fun deleteMarker(marker: MarkerEntity)

    @Query("SELECT * FROM markers")
    suspend fun getAllMarkers(): List<MarkerEntity>

    @Query("SELECT * FROM routes")
    suspend fun getAllRoutes(): List<RouteEntity>
}