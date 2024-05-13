package com.project.scanfinance.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.scanfinance.components.ExpenseTable
import com.project.scanfinance.database.Expense
import com.project.scanfinance.database.ExpenseDAO

@Composable
fun HomeActivity(navController: NavController, expenseDAO: ExpenseDAO) {

    Column(
        Modifier.padding(
            horizontal = 10.dp,
            vertical = 30.dp
        )
    ) {
        Text(
            text = "Scan Finance",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ExpenseTable(expenseDAO)
    }
}

