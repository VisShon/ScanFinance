package com.project.scanfinance.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BudgetInput() {
    var budget by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Budget Limit") },
            text = {
                Column {
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Handle saving the data here
                        showDialog = false
                    }
                ) {
                    Text("Set")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showDialog = true},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AEA8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Budget")
        }
    }
}
