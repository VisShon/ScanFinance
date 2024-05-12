package com.project.scanfinance.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetInput() {
    var budget by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = budget,
            onValueChange = { budget = it },
            label = { Text("Set Budget Limit") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Handle budget setting */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Budget")
        }
    }
}
