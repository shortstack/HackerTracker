package com.advice.schedule.models.local

sealed class LocationStatus {
    object Open : LocationStatus()
    object Closed : LocationStatus()
    object Mixed : LocationStatus()
    object Unknown : LocationStatus()
}