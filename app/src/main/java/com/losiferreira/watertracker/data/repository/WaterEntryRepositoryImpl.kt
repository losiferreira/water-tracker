package com.losiferreira.watertracker.data.repository

import com.losiferreira.watertracker.data.local.dao.WaterEntryDao
import com.losiferreira.watertracker.data.mapper.WaterEntryMapper
import com.losiferreira.watertracker.domain.model.WaterEntry
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.time.LocalDate

class WaterEntryRepositoryImpl(
    private val waterEntryDao: WaterEntryDao
) : WaterEntryRepository {

    override fun getEntryByDate(date: LocalDate): Maybe<WaterEntry> {
        return waterEntryDao.getEntryByDate(date)
            .map { WaterEntryMapper.toDomain(it) }
    }

    override fun observeEntryByDate(date: LocalDate): Flowable<WaterEntry> {
        return waterEntryDao.observeEntryByDate(date)
            .map { WaterEntryMapper.toDomain(it) }
            .switchIfEmpty(
                Flowable.just(WaterEntry(date = date, milliliters = 0))
            )
    }

    override fun getAllEntries(): Single<List<WaterEntry>> {
        return waterEntryDao.getAllEntries()
            .map { WaterEntryMapper.toDomainList(it) }
    }

    override fun observeAllEntries(): Flowable<List<WaterEntry>> {
        return waterEntryDao.observeAllEntries()
            .map { WaterEntryMapper.toDomainList(it) }
    }

    override fun getEntriesByMonth(yearMonth: String): Single<List<WaterEntry>> {
        return waterEntryDao.getEntriesByMonth(yearMonth)
            .map { WaterEntryMapper.toDomainList(it) }
    }

    override fun observeEntriesByMonth(yearMonth: String): Flowable<List<WaterEntry>> {
        return waterEntryDao.observeEntriesByMonth(yearMonth)
            .map { WaterEntryMapper.toDomainList(it) }
    }

    override fun saveEntry(entry: WaterEntry): Completable {
        val entity = WaterEntryMapper.toEntity(entry)
        return if (entry.id == 0L) {
            waterEntryDao.insertEntry(entity)
        } else {
            waterEntryDao.updateEntry(entity)
        }
    }

    override fun deleteEntry(entry: WaterEntry): Completable {
        return waterEntryDao.deleteEntry(WaterEntryMapper.toEntity(entry))
    }

    override fun deleteEntryByDate(date: LocalDate): Completable {
        return waterEntryDao.deleteEntryByDate(date)
    }

    fun removeDuplicateZeroEntries(): Completable {
        return waterEntryDao.removeDuplicateZeroEntries()
    }

    fun updateMillilitersByDate(date: LocalDate, milliliters: Int): Completable {
        return waterEntryDao.updateMillilitersByDate(date, milliliters)
    }
}