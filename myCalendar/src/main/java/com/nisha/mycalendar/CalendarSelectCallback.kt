package com.nisha.mycalendar

import java.util.Calendar

interface CalendarSelectCallback {
    fun onSelectTime(startDate: Calendar, endDate: Calendar)
}