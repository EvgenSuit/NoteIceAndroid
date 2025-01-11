package com.suit.noteice

import android.app.Application
import com.suit.noteice.koin.authModule
import com.suit.noteice.koin.notesModule
import com.suit.noteice.koin.utilsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NoteIceApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@NoteIceApplication)
            modules(
                utilsModule,
                authModule,
                notesModule)
        }
    }
}