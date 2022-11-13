package com.carepet.model

import com.datastax.oss.driver.api.core.cql.ResultSet
import com.datastax.oss.driver.api.mapper.annotations.*
import java.time.LocalDate
import java.util.*

@Dao
interface SensorAvgDAO {
    @Insert
    fun create(avg: SensorAvg?)

    @Update
    fun update(avg: SensorAvg?)

    @Select
    operator fun get(sensor: UUID?, date: LocalDate?, hour: Int): SensorAvg?

    @Query("SELECT value FROM sensor_avg WHERE sensor_id = :sensor AND date = :date")
    fun find(sensor: UUID, date: LocalDate): ResultSet
}