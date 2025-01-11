package com.suit.noteice.setup.ktor

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @param content content to be encoded to string by the mock
 */
inline fun <reified T> mockKtorEngine(
    content: T,
    status: HttpStatusCode,
) = MockEngine { request ->
    respond(
        content = ByteReadChannel(Json.encodeToString(content)),
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}