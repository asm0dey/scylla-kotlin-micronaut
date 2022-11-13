package com.carepet.model

import com.datastax.oss.driver.api.mapper.annotations.*
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang3.RandomUtils
import java.util.*
import kotlin.random.Random

@Entity
@CqlName("sensor")
class Sensor {
    @PartitionKey
    @JsonProperty("pet_id")
    var petId: UUID? = null

    @ClusteringColumn
    @JsonProperty("sensor_id")
    var sensorId: UUID? = null
    var type: String? = null

    constructor()
    constructor(petId: UUID?, sensorId: UUID?, type: String?) {
        this.petId = petId
        this.sensorId = sensorId
        this.type = type
    }

    fun randomData(): Float {
        return when (SensorType.byLetter(checkNotNull(type))) {
            SensorType.Temperature -> 101.0f + Random.nextInt(10) - 4 // average F
            SensorType.Pulse -> 100.0f + Random.nextInt(40) - 20 // average beat per minute
            SensorType.Respiration -> 35.0f + Random.nextInt(5) - 2 // average inhales per minute
            SensorType.Location -> 10.0f * (Random.nextFloat()) // pet can teleport
        }
    }

    override fun toString(): String {
        return "Sensor{petId=$petId, sensorId=$sensorId, type='$type'}"
    }

    companion object {
        fun random(petId: UUID?): Sensor {
            return Sensor(
                petId, UUID.randomUUID(), SensorType.values()[RandomUtils.nextInt(0, SensorType.values().size)].type
            )
        }
    }
}