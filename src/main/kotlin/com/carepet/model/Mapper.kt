package com.carepet.model

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory
import com.datastax.oss.driver.api.mapper.MapperBuilder as DatastaxOssDriverApiMapperMapperBuilder

@com.datastax.oss.driver.api.mapper.annotations.Mapper
interface Mapper {
    @DaoFactory
    fun owner(): OwnerDAO

    @DaoFactory
    fun pet(): PetDAO

    @DaoFactory
    fun sensor(): SensorDAO

    @DaoFactory
    fun measurement(): MeasureDAO

    @DaoFactory
    fun sensorAvg(): SensorAvgDAO

    companion object {
        fun builder(session: CqlSession): DatastaxOssDriverApiMapperMapperBuilder<Mapper> {
            return MapperBuilder(session)
        }
    }
}