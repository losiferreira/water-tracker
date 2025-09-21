package com.losiferreira.watertracker.data.mapper

import com.losiferreira.watertracker.data.local.entity.WaterEntryEntity
import com.losiferreira.watertracker.domain.model.WaterEntry

object WaterEntryMapper {
    fun toEntity(waterEntry: WaterEntry): WaterEntryEntity {
        return WaterEntryEntity(
            id = waterEntry.id,
            date = waterEntry.date,
            milliliters = waterEntry.milliliters,
            goalMilliliters = waterEntry.goalMilliliters
        )
    }

    fun toDomain(waterEntryEntity: WaterEntryEntity): WaterEntry {
        return WaterEntry(
            id = waterEntryEntity.id,
            date = waterEntryEntity.date,
            milliliters = waterEntryEntity.milliliters,
            goalMilliliters = waterEntryEntity.goalMilliliters
        )
    }

    fun toDomainList(entities: List<WaterEntryEntity>): List<WaterEntry> {
        return entities.map { toDomain(it) }
    }
}