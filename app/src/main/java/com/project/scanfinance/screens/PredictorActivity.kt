package com.project.scanfinance.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.project.scanfinance.components.BudgetInput
import com.project.scanfinance.components.BudgetPredictor
import com.project.scanfinance.components.ExpenseGraph
import com.project.scanfinance.ui.theme.ScanFinanceTheme

class PredictorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanFinanceTheme {
                PredictorScreen()
            }
        }
    }
}

@Composable
fun PredictorScreen() {
    Column {

        Text(
            text = "Budget Prediction",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        BudgetInput()
        ExpenseGraph()
        BudgetPredictor()
    }
}
