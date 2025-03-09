package com.suit.noteice.features.notes.domain

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.suit.noteice.utils.time.TestClock
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.util.Locale
import com.suit.noteice.R
import org.junit.Before
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class NoteTimeFormatterTests {
    private lateinit var noteTimeFormatter: NoteTimeFormatter
    private lateinit var resources: Resources
    private val testClock = TestClock()
    private var locale = Locale.US
    private val monthDayPattern = "MMM dd"

    private fun setup() {
        resources = ApplicationProvider.getApplicationContext<Context>().resources
        noteTimeFormatter = NoteTimeFormatter(
            locale = locale,
            clock = testClock,
            resources = resources
        )
    }

    @Before
    fun init() {
        setup()
    }

    @Test
    fun sameDay_todayDisplayed() {
        val targetInstant = Instant.ofEpochSecond(60*60)
        testClock.setInstant(Instant.ofEpochSecond(0))
        
        val result = noteTimeFormatter.format(targetInstant)
        val sdf = SimpleDateFormat("'${resources.getString(R.string.today)}', HH:mm", locale)
        assertEquals(sdf.format(Date.from(targetInstant)), result)
    }
    @Test
    fun differentDay_sameYear_monthAndDayDisplayed() {
        val sdf = SimpleDateFormat("$monthDayPattern, HH:mm", locale)
        val targetInstant = Instant.ofEpochSecond(60*60*24)
        testClock.setInstant(Instant.ofEpochSecond(0))

        val result = noteTimeFormatter.format(targetInstant)
        assertEquals(sdf.format(Date.from(targetInstant)), result)
    }
    @Test
    fun differentDay_differentYear_monthDayAndYearDisplayed() {
        val sdf = SimpleDateFormat("$monthDayPattern, yyyy, HH:mm", locale)
        val targetInstant = Instant.ofEpochSecond(60*60*24*365)
        testClock.setInstant(Instant.ofEpochSecond(0))

        val result = noteTimeFormatter.format(targetInstant)
        assertEquals(sdf.format(Date.from(targetInstant)), result)
    }
}