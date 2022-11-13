package com.carepet.model

import com.datastax.oss.driver.api.core.cql.ResultSet
import com.datastax.oss.driver.api.mapper.annotations.*
import java.time.Instant
import java.util.*

@Dao
interface MeasureDAO {
    @Insert
    fun create(measure: Measure?)

    @Update
    fun update(measure: Measure?)

    @Select
    operator fun get(sensor: UUID?, ts: Instant?): Measure?

    @Query("SELECT value FROM measurement WHERE sensor_id = :sensor AND ts >= :start AND ts <= :end")
    fun find(sensor: UUID, start: Instant, end: Instant): ResultSet

    @Query("SELECT ts, value FROM measurement WHERE sensor_id = :sensor AND ts >= :start AND ts <= :end")
    fun findWithTimestamps(sensor: UUID, start: Instant, end: Instant): ResultSet
}