@file:OptIn(ExperimentalCli::class, InternalAPI::class, ExperimentalStreamsApi::class)

package rkncli

import io.ktor.util.*
import io.rsocket.kotlin.*
import kotlinx.cli.*
import kotlinx.coroutines.flow.*

class RequestStreamCommand : RSocketSubcommand("rs", "Do request stream") {

    private val limit by option(ArgType.Int, description = "Limit of request").default(10)
    private val requestBy by option(ArgType.Int, description = "Request by amount")

    override suspend fun exec(rSocket: RSocket) {
        rSocket.requestStream(firstPayload())
            .flowOn(PrefetchStrategy(requestBy ?: limit, 0))
            .take(limit)
            .collect {
                println(
                    """
                    Payload(
                      data=      `${it.data.readText()}`
                      metadata=  `${it.metadata?.readText()}`
                    )
                    """.trimIndent()
                )
            }
    }
}
