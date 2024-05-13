package com.project.scanfinance.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "payment_to") val paymentTo: String,
    @ColumnInfo(name = "payment_from") val paymentFrom: String,
    @ColumnInfo(name = "amount_paid") val amountPaid: Double
)
