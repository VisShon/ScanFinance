package com.project.scanfinance.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    val date: String,
    val paymentTo: String,
    val paymentFrom: String,
    val amountPaid: Double
)


