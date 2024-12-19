package com.example.mapnavigationapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markers")
data class MarkerEntity(
    @PrimaryKey val id: Int,
    val latitude: Double,
    val longitude: Double,
    val title: String
)