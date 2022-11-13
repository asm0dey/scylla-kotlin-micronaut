package com.carepet

import com.carepet.model.*
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder
import com.datastax.oss.driver.api.core.cql.BatchType
import org.slf4j.LoggerFactory
import picocli.CommandLine.Option
import java.time.Duration
import java.time.Instant


fun main(args: Array<String>) {
    val client = Sensor(Config.parse(Sensor.SensorConfig(), args))
    client.save()
    client.run()
}

private val LOG = LoggerFactory.getLogger(Sensor::class.java)

class Sensor(private val config: SensorConfig) {
    private val owner: Owner = Owner.random()
    private val pet: Pet = Pet.random(owner.ownerId)
    private val sensors = buildList {
        repeat(SensorType.values().size) {
            add(com.carepet.model.Sensor.random(pet.petId))
        }
    }

    /**
     * Initiates a connection to the session specified by the application.conf.
     */
    private val keyspace: CqlSession
        get() = config.builder(Config.keyspace).build()

    /**
     * Save owner, pet and sensors to the database.
     */
    internal fun save() {

        keyspace.use { session ->
            val m: Mapper = Mapper.builder(session).build()
            LOG.info("owner = $owner")
            LOG.info("pet = $pet")
            m.owner().create(owner)
            m.pet().create(pet)
            for (s in sensors) {
                LOG.info("sensor = $s")
                m.sensor().create(s)
            }
        }
    }

    /**
     * Generate random sensors data and push it to the app.
     */
    internal fun run() {
        keyspace.use { session ->
            val statement = session.prepare("INSERT INTO measurement (sensor_id, ts, value) VALUES (?, ?, ?)")
            var builder = BatchStatementBuilder(BatchType.UNLOGGED)
            val measures = ArrayList<Measure>()
            var prev = Instant.now()
            while (true) {
                while (Duration.between(prev, Instant.now()) < config.bufferInterval) {
                    Thread.sleep(config.measurement?.toMillis() ?: return)
                    for (s in sensors) {
                        val m = readSensorData(s)
                        measures.add(m)
                        LOG.info(m.toString())
                    }
                }
                prev = prev.plusMillis(
                    Duration.between(prev, Instant.now())
                        .toMillis() / config.bufferInterval!!.toMillis() * config.bufferInterval!!.toMillis()
                )
                LOG.info("pushing data")
                // this is simplified example of batch execution. standard
                // best practice is to batch values that end up in the same partition:
                // https://www.scylladb.com/2019/03/27/best-practices-for-scylla-applications/
                for (m in measures) {
                    builder = builder.addStatement(statement.bind(m.sensorId, m.ts, m.value))
                }
                session.execute(builder.build())
                builder.clearStatements()
                measures.clear()
            }
        }
    }

    private fun sleep(d: Duration?): Boolean {
        return try {
            Thread.sleep(d!!.toMillis())
            true
        } catch (e: InterruptedException) {
            false
        }
    }

    private fun readSensorData(s: com.carepet.model.Sensor): Measure {
        return Measure(s.sensorId, Instant.now(), s.randomData())
    }

    class SensorConfig : Config() {
        @Option(names = ["--buffer-interval"], description = ["buffer to accumulate measures"], defaultValue = "PT1H")
        var bufferInterval: Duration? = null

        @Option(names = ["--measure"], description = ["sensors measurement interval"], defaultValue = "PT1M")
        var measurement: Duration? = null
    }


}
