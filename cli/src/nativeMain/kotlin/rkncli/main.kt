@file:OptIn(ExperimentalCli::class, InternalAPI::class, ExperimentalStreamsApi::class)

package rkncli

import io.ktor.util.*
import io.rsocket.kotlin.*
import kotlinx.cli.*

fun main(args: Array<String>) {
    val parser = ArgParser("rkncli")
    parser.subcommands(RequestStreamCommand())
    val testArgs = arrayOf(
        "rs",
        "0.0.0.0",
        "9000",
        "--setupData", "wss://rsocket-demo.herokuapp.com/rsocket",
        "--route", "searchTweets",
        "--data", "Sunday"
    )
    val a1 = arrayOf("-h")


    parser.parse(args)
}
