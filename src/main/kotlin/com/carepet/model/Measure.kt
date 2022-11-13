package com.carepet.model

import com.datastax.oss.driver.api.mapper.annotations.*
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.util.*

@Entity
@CqlName("measurement")
class Measure {
    @PartitionKey
    @JsonProperty("sensor_id")
    var sensorId: UUID? = null

    @ClusteringColumn
    var ts: Instant? = null
    var value = 0f

    constructor()
    constructor(sensorId: UUID?, ts: Instant?, value: Float) {
        this.sensorId = sensorId
        this.ts = ts
        this.value = value
    }

    override fun toString(): String {
        return "Measure{sensorId=$sensorId, ts=$ts, value=$value}"
    }
}