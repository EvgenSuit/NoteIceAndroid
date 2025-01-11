package com.suit.noteice.koin

import com.suit.noteice.features.auth.domain.AuthClient
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

val authModule = module {
    single {
        AuthClient(
            engine = CIO.create(),
            tokensManager = get()
        )
    }
}