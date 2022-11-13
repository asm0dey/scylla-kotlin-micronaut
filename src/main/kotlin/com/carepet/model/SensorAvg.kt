package com.carepet.model

import com.datastax.oss.driver.api.mapper.annotations.*
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.*

@Entity
@CqlName("sensor_avg")
class SensorAvg {
    @PartitionKey
    @JsonProperty("sensor_id")
    var sensorId: UUID? = null

    @ClusteringColumn(0)
    var date: LocalDate? = null

    @ClusteringColumn(1)
    var hour = 0
    var value = 0f

    constructor()
    constructor(sensorId: UUID?, date: LocalDate?, hour: Int, value: Float) {
        this.sensorId = sensorId
        this.date = date
        this.hour = hour
        this.value = value
    }

    override fun toString(): String {
        return "SensorAvg{sensorId=$sensorId, date=$date, hour=$hour, value=$value}"
    }
}