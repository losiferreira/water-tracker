package com.losiferreira.watertracker.domain.repository

import com.losiferreira.watertracker.domain.model.WaterEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.time.LocalDate

interface WaterEntryRepository {
    fun getEntryByDate(date: LocalDate): Maybe<WaterEntry>
    fun observeEntryByDate(date: LocalDate): Flowable<WaterEntry>
    fun getAllEntries(): Single<List<WaterEntry>>
    fun observeAllEntries(): Flowable<List<WaterEntry>>
    fun getEntriesByMonth(yearMonth: String): Single<List<WaterEntry>>
    fun observeEntriesByMonth(yearMonth: String): Flowable<List<WaterEntry>>
    fun saveEntry(entry: WaterEntry): Completable
    fun deleteEntry(entry: WaterEntry): Completable
    fun deleteEntryByDate(date: LocalDate): Completable
    fun removeDuplicateZeroEntries(): Completable
    fun updateMillilitersByDate(date: LocalDate, milliliters: Int): Completable
}