package com.suit.noteice.koin

import com.suit.noteice.features.notes.domain.NotesRepository
import com.suit.noteice.features.notes.presentation.NotesViewModel
import org.koin.dsl.module

val notesModule = module {
    single {
        NotesRepository(
            notesClient = get()
        )
    }
    factory {
        NotesViewModel(
            notesRepository = get()
        )
    }
}