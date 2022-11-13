package com.carepet

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata
import org.slf4j.LoggerFactory


fun main(args: Array<String>) {
    val config: Config = Config.parse(Config(), args)
    val client = Migrate(config)
    client.createKeyspace()
    client.createSchema()
    client.printMetadata()
}

private val LOG = LoggerFactory.getLogger(Migrate::class.java)

class Migrate(private val config: Config) {

    /**
     * Initiates a connection to the session specified by the application.conf.
     */
    private fun connect(): CqlSession {
        return config.builder().build()
    }

    /**
     * Initiates a connection to the session specified by the application.conf.
     */
    private fun keyspace(): CqlSession = config.builder(Config.keyspace).build()

    /**
     * Creates the keyspace for this example.
     */
    fun createKeyspace() {
        LOG.info("creating keyspace...")
        connect().use { session -> session.execute(Config.getResource("care-pet-keyspace.cql")!!) }
    }

    /**
     * Creates the tables for this example.
     */
    fun createSchema() {
        LOG.info("creating table...")
        keyspace().use { session ->
            for (cql in Config.getResource("care-pet-ddl.cql")!!
                .split(';')
                .dropLastWhile { it.isEmpty() }
            ) {
                session.execute(cql)
            }
        }
    }

    /**
     * Prints keyspace metadata.
     */
    fun printMetadata() {
        keyspace().use { session ->
            val keyspace: KeyspaceMetadata = session.metadata.getKeyspace(Config.Companion.keyspace).get()
            for (table in keyspace.tables.values) {
                System.out.printf("Keyspace: %s; Table: %s%n", keyspace.name, table.name)
            }
        }
    }

}
