package com.suit.noteice.features.notes.domain

import android.content.res.Resources
import com.suit.noteice.R
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NoteTimeFormatter(
    private val locale: Locale,
    private val clock: Clock,
    private val resources: Resources
) {
    fun format(timeToFormat: Instant): String {
        val currTime = Date.from(clock.instant())
        val currCalendar = Calendar.getInstance().apply {
            time = currTime
        }
        val targetCalendar = Calendar.getInstance().apply {
            time = Date.from(timeToFormat)
        }
        val isDaySame = currCalendar.get(Calendar.DAY_OF_YEAR) == targetCalendar.get(Calendar.DAY_OF_YEAR)
        val isYearSame = currCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR)
        val pattern = if (!isYearSame) {
                "MMM dd, yyyy, HH:mm"
            } else {
                if (!isDaySame) "MMM dd, HH:mm" else "'${resources.getString(R.string.today)}', HH:mm"
            }
        return SimpleDateFormat(pattern, locale).format(Date.from(timeToFormat))
    }
}