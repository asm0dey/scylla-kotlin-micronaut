package com.carepet.model

import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import com.datastax.oss.driver.api.mapper.annotations.Update
import java.util.*

@Dao
interface OwnerDAO {
    @Insert
    fun create(owner: Owner?)

    @Update
    fun update(owner: Owner?)

    @Select
    operator fun get(id: UUID?): Owner?
}