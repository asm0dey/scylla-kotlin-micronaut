package com.carepet.server

import com.carepet.model.*
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.Row
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.Validated
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject
import java.time.*
import java.util.*
import javax.validation.constraints.NotBlank

@Controller("/api")
@Validated
class ModelController @Inject constructor(private val session: CqlSession) {
    private val mapper: Mapper = Mapper.builder(session).build()

    @Get(uri = "/owner/{id}", produces = [MediaType.APPLICATION_JSON])
    fun owner(id: @NotBlank UUID?): Single<Owner>? {
        return mapper.owner()[id]?.let { Single.just(it) }
    }

    @Get(uri = "/owner/{id}/pets", produces = [MediaType.APPLICATION_JSON])
    fun pets(id: @NotBlank UUID?): Observable<Pet> {
        return mapper.pet().findByOwner(id)?.let { Observable.fromIterable(it) } ?: Observable.empty()
    }

    @Get(uri = "/pet/{id}/sensors", produces = [MediaType.APPLICATION_JSON])
    fun sensors(id: @NotBlank UUID?): Observable<Sensor> {
        return mapper.sensor().findByPet(id)?.let { Observable.fromIterable(it) } ?: Observable.empty()
    }

    @Get(uri = "/sensor/{id}/values", produces = [MediaType.APPLICATION_JSON])
    fun values(
        id: @NotBlank UUID,
        @QueryValue from: @NotBlank String,
        @QueryValue to: @NotBlank String
    ): Observable<Float> {
        val res = mapper.measurement().find(id, Instant.parse(from), Instant.now())
        return Observable.fromIterable(res.map { x: Row -> x.getFloat(0) })
    }

    @Get(uri = "/sensor/{id}/values/day/{day}", produces = [MediaType.APPLICATION_JSON])
    fun avg(id: @NotBlank UUID, day: @NotBlank String): Observable<Float> {
        val date = LocalDate.parse(day)
        if (date.isAfter(LocalDate.now())) {
            throw HttpStatusException(HttpStatus.BAD_REQUEST, "request into the future")
        }
        val res = mapper.sensorAvg().find(id, date)
        var data = res.map { x: Row -> x.getFloat(0) }.all()
        if (data.size != 24) {
            data = ArrayList(data)
            aggregate(id, date, data)
        }
        return Observable.fromIterable(data)
    }

    fun aggregate(id: UUID, day: LocalDate, data: MutableList<Float>) {
        val now = LocalDateTime.now(Clock.systemUTC())

        // can't aggregate data for post today's date
        if (day.dayOfYear > now.dayOfYear) {
            throw HttpStatusException(HttpStatus.BAD_REQUEST, "request into the future")
        }

        // we can start from next missing hour. hours = [0, 23]. len = [0, 24]
        val startHour = data.size
        val startDate = day.atStartOfDay().toInstant(ZoneOffset.UTC)
        val endDate = day.atTime(23, 59, 59, 999999999).toInstant(ZoneOffset.UTC)
        val measures = mapper.measurement().findWithTimestamps(id, startDate, endDate)
            .map { row: Row -> Measure(null, row.getInstant(0), row.getFloat(1)) }
            .all()
        val prevAvgSize = data.size
        groupBy(data, measures, startHour, day, now)
        saveAggregate(id, data, prevAvgSize, day, now)
    }

    // saveAggregate saves the result monotonically sequentially to the database
    private fun saveAggregate(
        sensorId: UUID?,
        data: List<Float>,
        prevAvgSize: Int,
        day: LocalDate,
        now: LocalDateTime
    ) {
        // if it's the same day, we can't aggregate current hour
        val sameDate = now.dayOfYear == day.dayOfYear
        val current = now.hour
        for (hour in prevAvgSize until data.size) {
            if (sameDate && hour >= current) {
                break
            }
            mapper.sensorAvg().create(SensorAvg(sensorId, day, hour, data[hour]))
        }
    }

    companion object {
        private fun groupBy(
            data: MutableList<Float>,
            measures: List<Measure>,
            startHour: Int,
            day: LocalDate,
            now: LocalDateTime
        ) {
            // if it's the same day, we can't aggregate current hour
            val sameDate = now.dayOfYear == day.dayOfYear
            val last = now.hour

            class Avg {
                var value = 0.0
                var total = 0
            }

            // aggregate data
            val ag = arrayOfNulls<Avg>(24)
            for (m in measures) {
                val hour = m.ts!!.atOffset(ZoneOffset.UTC).hour
                if (ag[hour] == null) {
                    ag[hour] = Avg()
                }
                val a = ag[hour]
                a!!.total++
                a.value += m.value.toDouble()
            }

            // ensure data completeness
            for (hour in startHour..23) {
                if (!sameDate || hour <= last) {
                    if (ag[hour] == null) {
                        ag[hour] = Avg()
                    }
                }
            }

            // fill the avg
            var hour = startHour
            while (hour < ag.size && ag[hour] != null) {
                val a = ag[hour]
                if (a!!.total > 0) {
                    data.add((a.value / a.total).toFloat())
                } else {
                    data.add(0.0f)
                }
                hour++
            }
        }
    }
}