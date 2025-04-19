package com.example.lab4

import java.time.LocalDate

data class Task(
    val title: String,
    val description: String,
    val dueDate: LocalDate? = null,
    val createdAt: LocalDate = LocalDate.now()
)
