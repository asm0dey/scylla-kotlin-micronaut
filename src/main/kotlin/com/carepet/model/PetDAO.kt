package com.carepet.model

import com.datastax.oss.driver.api.core.PagingIterable
import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import com.datastax.oss.driver.api.mapper.annotations.Update
import java.util.*

@Dao
interface PetDAO {
    @Insert
    fun create(pet: Pet?)

    @Update
    fun update(pet: Pet?)

    @Select
    operator fun get(owner: UUID?, id: UUID?): Pet?

    @Select(customWhereClause = "owner_id = :owner")
    fun findByOwner(owner: UUID?): PagingIterable<Pet?>?
}