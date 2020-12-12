@file:OptIn(ExperimentalCli::class, InternalAPI::class, ExperimentalStreamsApi::class, ExperimentalMetadataApi::class)

package rkncli

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.*
import io.rsocket.kotlin.core.*
import io.rsocket.kotlin.logging.*
import io.rsocket.kotlin.metadata.*
import io.rsocket.kotlin.payload.*
import io.rsocket.kotlin.transport.ktor.*
import kotlinx.cli.*
import kotlinx.coroutines.*

abstract class RSocketSubcommand(
    name: String,
    description: String,
) : Subcommand(name, description) {
    private val host by argument(ArgType.String, description = "Hostname of resource to call")
    private val port by argument(ArgType.Int, description = "Port of resource to call")
    private val debug by option(ArgType.Boolean, description = "Turn on debug mode").default(false)
    private val setupData by option(ArgType.String, description = "Data to setup connection")

    private val route by option(ArgType.String, description = "Route")
    private val data by option(ArgType.String, description = "Payload data")

    fun firstPayload(): Payload = buildPayload {
        data?.let(this::data) ?: data(ByteReadPacket.Empty)
        compositeMetadata {
            route?.let { add(RoutingMetadata(it)) }
        }
    }

    abstract suspend fun exec(rSocket: RSocket)

    override fun execute() {
        val connector = RSocketConnector {
            if (debug) loggerFactory = PrintLogger.withLevel(LoggingLevel.DEBUG)
            connectionConfig {
                payloadMimeType = PayloadMimeType(
                    data = WellKnownMimeType.ApplicationJson,
                    metadata = WellKnownMimeType.MessageRSocketCompositeMetadata
                )
                setupData?.let {
                    setupPayload {
                        buildPayload { data(it) }
                    }
                }
            }
        }
        SelectorManager().use { selector ->
            val transport = aSocket(selector).tcp().clientTransport(host, port)
            runBlocking {
                val rSocket = connector.connect(transport)
                val result = runCatching { exec(rSocket) }
                rSocket.cancelAndJoin()
                result.getOrThrow()
            }
        }
    }
}
