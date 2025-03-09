package com.suit.noteice.koin

import com.suit.noteice.features.notes.domain.NoteTimeFormatter
import com.suit.noteice.features.notes.domain.NotesRepository
import com.suit.noteice.features.notes.presentation.NotesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.time.Clock
import java.util.Locale

val notesModule = module {
    single {
        NotesRepository(
            notesClient = get()
        )
    }
    factory {
        NoteTimeFormatter(
            locale = Locale.getDefault(),
            clock = Clock.systemDefaultZone(),
            resources = androidContext().resources
        )
    }
    factory {
        NotesViewModel(
            notesRepository = get(),
            noteTimeFormatter = get()
        )
    }
}