package rkncli.proxy

import io.ktor.client.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.rsocket.kotlin.core.*
import io.rsocket.kotlin.logging.*
import io.rsocket.kotlin.transport.ktor.*
import io.rsocket.kotlin.transport.ktor.client.*
import kotlinx.coroutines.*
import io.ktor.client.engine.cio.CIO as ClientCIO
import io.ktor.client.features.websocket.WebSockets as ClientWebSockets
import io.rsocket.kotlin.transport.ktor.client.RSocketSupport as ClientRSocketSupport

suspend fun main() {
    ActorSelectorManager(Dispatchers.IO).use { selector ->
        val transport = aSocket(selector).tcp().serverTransport(port = 9000)

        RSocketServer().bind(transport) {
            val url = config.setupPayload.data.readText()
            val httpClient = HttpClient(ClientCIO) {
                install(ClientWebSockets)
                install(ClientRSocketSupport) {
                    connector = RSocketConnector {
                        loggerFactory = PrintLogger.withLevel(LoggingLevel.DEBUG)
                        connectionConfig {
                            payloadMimeType = config.payloadMimeType
                        }
                    }
                }
            }
            println("route: $url")
            runBlocking { httpClient.rSocket(url) }
        }.join()
    }
}
