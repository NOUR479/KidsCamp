package com.example.kidscamp.model

data class Workshop(
    val id: String,
    val title: String,
    val category: String,
    val day: String,
    val startHour: Int,
    val endHour: Int,
    val minAge: Int,
    val maxAge: Int,
    val capacity: Int,
    val price: Double
)