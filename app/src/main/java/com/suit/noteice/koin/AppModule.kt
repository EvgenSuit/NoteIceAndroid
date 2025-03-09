package com.suit.noteice.koin

import org.koin.dsl.module

val appModule = module {
    includes(utilsModule, authModule, notesModule)
}