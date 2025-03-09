package com.suit.noteice.utils.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class TestClock: Clock() {
    private var instant: Instant = Instant.now()
    private var zoneId: ZoneId = ZoneId.systemDefault()
    override fun instant(): Instant = instant

    fun setInstant(inputInstant: Instant) {
        instant = inputInstant
    }
    fun setZone(inputZoneId: ZoneId) {
        zoneId = inputZoneId
    }

    override fun withZone(zone: ZoneId?): Clock {
        return Clock.system(zone)
    }

    override fun getZone(): ZoneId {
        return zoneId
    }
}