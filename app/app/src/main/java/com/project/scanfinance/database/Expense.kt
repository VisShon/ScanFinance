package com.project.scanfinance.database

data class Expense(
    val date: String,
    val paymentTo: String,
    val paymentFrom: String,
    val amountPaid: Double
)
