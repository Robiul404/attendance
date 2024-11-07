package com.example.attendance

data class AttendanceRecord(
    val roll: String,
    val name: String,
    val presentDates: List<String> // List of dates the student was present
)
