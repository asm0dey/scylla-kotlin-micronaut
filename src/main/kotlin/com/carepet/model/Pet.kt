package com.carepet.model

import com.datastax.oss.driver.api.mapper.annotations.*
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang3.RandomStringUtils
import java.util.*
import kotlin.random.Random

@Entity
@CqlName("pet")
class Pet {
    @PartitionKey
    @JsonProperty("owner_id")
    var ownerId: UUID? = null

    @ClusteringColumn
    @JsonProperty("pet_id")
    var petId: UUID? = null

    @JsonProperty("chip_id")
    var chipId: String? = null
    var species: String? = null
    var breed: String? = null
    var color: String? = null
    var gender: String? = null
    var age = 0
    var weight = 0f
    var address: String? = null
    var name: String? = null

    constructor()
    constructor(
        ownerId: UUID?,
        petId: UUID?,
        chipId: String?,
        species: String?,
        breed: String?,
        color: String?,
        gender: String?,
        age: Int,
        weight: Float,
        address: String?,
        name: String?
    ) {
        this.ownerId = ownerId
        this.petId = petId
        this.chipId = chipId
        this.species = species
        this.breed = breed
        this.color = color
        this.gender = gender
        this.age = age
        this.weight = weight
        this.address = address
        this.name = name
    }

    override fun toString(): String {
        return "Pet{ownerId=$ownerId, petId=$petId, chipId='$chipId', species='$species', breed='$breed', color='$color', gender='$gender', age=$age, weight=$weight, address='$address', name='$name'}"
    }

    companion object {
        fun random(ownerId: UUID?): Pet {
            return Pet(
                ownerId,
                UUID.randomUUID(),
                "",
                "",
                "",
                "",
                "",
                1 + Random.nextInt(0, 100),
                5.0f + 10.0f * Random.nextFloat(),
                "home",
                RandomStringUtils.randomAlphanumeric(8)
            )
        }
    }
}