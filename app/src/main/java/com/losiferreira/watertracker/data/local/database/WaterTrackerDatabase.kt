package com.losiferreira.watertracker.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.losiferreira.watertracker.data.local.converter.DateConverter
import com.losiferreira.watertracker.data.local.dao.WaterEntryDao
import com.losiferreira.watertracker.data.local.entity.WaterEntryEntity

@Database(
    entities = [WaterEntryEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class WaterTrackerDatabase : RoomDatabase() {
    abstract fun waterEntryDao(): WaterEntryDao

    companion object {
        const val DATABASE_NAME = "water_tracker_db"
    }
}