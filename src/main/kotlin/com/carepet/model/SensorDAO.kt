package com.carepet.model

import com.datastax.oss.driver.api.core.PagingIterable
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import com.datastax.oss.driver.api.mapper.annotations.Update
import java.util.*

@Dao
interface SensorDAO {
    @Insert
    fun create(sensor: Sensor?)

    @Update
    fun update(sensor: Sensor?)

    @Select
    operator fun get(pet: UUID?, id: UUID?): Sensor?

    @Select(customWhereClause = "pet_id = :pet")
    fun findByPet(pet: UUID?): PagingIterable<Sensor?>?
}