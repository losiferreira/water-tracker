package com.losiferreira.watertracker.data.local.dao

import androidx.room.*
import com.losiferreira.watertracker.data.local.entity.WaterEntryEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.time.LocalDate

@Dao
interface WaterEntryDao {
    @Query("SELECT * FROM water_entries WHERE date = :date")
    fun getEntryByDate(date: LocalDate): Maybe<WaterEntryEntity>

    @Query("SELECT * FROM water_entries WHERE date = :date")
    fun observeEntryByDate(date: LocalDate): Flowable<WaterEntryEntity>

    @Query("SELECT * FROM water_entries ORDER BY date DESC")
    fun getAllEntries(): Single<List<WaterEntryEntity>>

    @Query("SELECT * FROM water_entries ORDER BY date DESC")
    fun observeAllEntries(): Flowable<List<WaterEntryEntity>>

    @Query("SELECT * FROM water_entries WHERE date LIKE :yearMonth || '%' ORDER BY date DESC")
    fun getEntriesByMonth(yearMonth: String): Single<List<WaterEntryEntity>>

    @Query("SELECT * FROM water_entries WHERE date LIKE :yearMonth || '%' ORDER BY date DESC")
    fun observeEntriesByMonth(yearMonth: String): Flowable<List<WaterEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertEntry(entry: WaterEntryEntity): Completable

    @Update
    fun updateEntry(entry: WaterEntryEntity): Completable

    @Delete
    fun deleteEntry(entry: WaterEntryEntity): Completable

    @Query("DELETE FROM water_entries WHERE date = :date")
    fun deleteEntryByDate(date: LocalDate): Completable

    @Query("DELETE FROM water_entries WHERE milliliters = 0 AND id NOT IN (SELECT MIN(id) FROM water_entries WHERE milliliters = 0 GROUP BY date)")
    fun removeDuplicateZeroEntries(): Completable

    @Query("UPDATE water_entries SET milliliters = :milliliters WHERE date = :date")
    fun updateMillilitersByDate(date: LocalDate, milliliters: Int): Completable
}