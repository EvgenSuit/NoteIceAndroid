package com.suit.noteice.koin

import com.suit.noteice.features.auth.domain.AuthClient
import com.suit.noteice.features.auth.domain.AuthRepository
import com.suit.noteice.features.auth.presentation.AuthViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module
import java.util.Locale


val authModule = module {
    single {
        AuthClient(
            engine = CIO.create(),
            tokensManager = get(),
            locale = Locale.getDefault()
        )
    }
    single {
        AuthRepository(authClient = get())
    }
    factory {
        AuthViewModel(
            authRepository = get()
        )
    }
}