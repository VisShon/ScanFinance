package com.project.scanfinance.components


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BudgetPredictor() {
    val nextMonthPrediction = "Estimated Budget for Next Month: $500"

    Text(nextMonthPrediction)
}
