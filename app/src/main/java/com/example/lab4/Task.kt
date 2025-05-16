package com.example.lab4

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.UUID

@Parcelize
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val dueDate: LocalDate? = null,
    val createdAt: LocalDate = LocalDate.now(),
    var isCompleted: Boolean = false
) : Parcelable