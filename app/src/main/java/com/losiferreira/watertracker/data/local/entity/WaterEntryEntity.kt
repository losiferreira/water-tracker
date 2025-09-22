package com.losiferreira.watertracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDate

@Entity(
    tableName = "water_entries",
    indices = [Index(value = ["date"], unique = true)]
)
data class WaterEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val milliliters: Int,
    val goalMilliliters: Int = 2500
)