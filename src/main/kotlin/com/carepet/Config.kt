package com.carepet

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import picocli.CommandLine
import picocli.CommandLine.Option
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors
import kotlin.system.exitProcess

open class Config {
    @Option(names = ["--hosts"], description = ["database contact points"])
    var hosts: Array<String?>? = null

    @Option(names = ["-dc", "--datacenter"], description = ["local datacenter name for default profile"])
    var datacenter: String? = null

    @Option(names = ["-u", "--username"], description = ["password based authentication username"])
    var username: String? = null

    @Option(names = ["-p", "--password"], description = ["password based authentication password"])
    var password: String? = null

    /**
     * Builds configured CqlSession builder to acquire a new session.
     */
    @JvmOverloads
    fun builder(keyspace: String? = null): CqlSessionBuilder {
        var builder = CqlSession.builder()
            .withApplicationName(applicationName)
            .withClientId(clientId)
        val currentHosts = hosts
        if (!currentHosts.isNullOrEmpty()) {
            builder = builder
                .addContactPoints(currentHosts.map { it!!.toAddress() })
                .withLocalDatacenter(datacenter!!)
        }
        if (!username.isNullOrEmpty()) {
            builder = builder.withAuthCredentials(username!!, password!!)
        }
        if (!keyspace.isNullOrEmpty()) {
            builder = builder.withKeyspace(keyspace)
        }
        return builder
    }

    companion object {
        const val keyspace = "carepet"
        const val applicationName = "care-pet"
        val clientId: UUID = UUID.randomUUID()
        private const val port = 9042

        /**
         * Parses arguments into a new instance of the [Config] object.
         */
        fun <T> parse(command: T, args: Array<String>): T {
            val cmd = CommandLine(command)
            cmd.isUnmatchedArgumentsAllowed = false
            cmd.parseArgs(*args)
            if (cmd.isUsageHelpRequested) {
                cmd.usage(System.err)
                exitProcess(1)
            }
            return command
        }

        /**
         * Transforms an address of the form host:port into an InetSocketAddress.
         */
        fun String.toAddress(): InetSocketAddress {
            val uri = URI("scylladb://" + withPort(port))
            val host = uri.host
            val port = uri.port
            if (uri.host.isNullOrEmpty() || uri.port == -1) {
                throw URISyntaxException(uri.toString(), "URI must have host and port")
            }
            return InetSocketAddress(host, port)
        }

        /**
         * Ensures an address has port provided.
         */
        private fun String.withPort(port: Int): String =
            if (contains(":")) this else "${this}:$port"


        /**
         * Loads a resource content.
         */
        fun getResource(name: String?): String? = getResourceFileAsString(name)

        /**
         * Reads given resource file as a string.
         */
        private fun getResourceFileAsString(name: String?): String? {
            val classLoader = ClassLoader.getSystemClassLoader()
            classLoader.getResourceAsStream(name).use { input ->
                if (input == null) return null
                InputStreamReader(input, StandardCharsets.UTF_8).use { isr ->
                    val reader = BufferedReader(isr)
                    return reader.lines().collect(Collectors.joining(System.lineSeparator()))
                }
            }
        }
    }
}