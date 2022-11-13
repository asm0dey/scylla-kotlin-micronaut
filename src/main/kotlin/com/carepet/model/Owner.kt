package com.carepet.model

import com.datastax.oss.driver.api.mapper.annotations.*
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang3.RandomStringUtils
import java.util.*

@Entity
@CqlName("owner")
class Owner {
    @PartitionKey
    @JsonProperty("owner_id")
    var ownerId: UUID? = null
    var name: String? = null
    var address: String? = null

    constructor(ownerId: UUID?, name: String?, address: String?) {
        this.ownerId = ownerId
        this.name = name
        this.address = address
    }

    constructor()

    override fun toString(): String {
        return "Owner{ownerId=$ownerId, name='$name', address='$address'}"
    }

    companion object {
        fun random(): Owner {
            return Owner(
                UUID.randomUUID(), RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(10)
            )
        }
    }
}