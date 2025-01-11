package com.suit.noteice.koin

import com.suit.noteice.utils.notes.NotesClient
import com.suit.noteice.utils.notes.TokensManager
import com.suit.noteice.utils.notes.tokensDataStore
import io.ktor.client.engine.cio.CIO
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {
    single {
        TokensManager(
            tokensDataStore = androidContext().tokensDataStore
        )
    }
    single {
        NotesClient(
            refreshTokenEngine = CIO.create(),
            notesEngine = CIO.create(),
            tokensManager = get()
        )
    }
}