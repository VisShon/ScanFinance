package com.project.scanfinance.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.scanfinance.database.Expense
import com.project.scanfinance.database.ExpenseDAO
import kotlinx.coroutines.launch


@Composable
fun ExpenseTable(dao: ExpenseDAO) {
    var showDialog by remember { mutableStateOf(false) }
    var newDate by remember { mutableStateOf("") }
    var newPaymentTo by remember { mutableStateOf("") }
    var newPaymentFrom by remember { mutableStateOf("") }
    var newAmountPaid by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val expenses = remember { mutableStateOf<List<Expense>>(emptyList()) }

    LaunchedEffect(expenses) {
        expenses.value = dao.getAllExpenses()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Expense") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDate,
                        onValueChange = { newDate = it },
                        label = { Text("Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPaymentTo,
                        onValueChange = { newPaymentTo = it },
                        label = { Text("Payment To") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPaymentFrom,
                        onValueChange = { newPaymentFrom = it },
                        label = { Text("Payment From") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAmountPaid,
                        onValueChange = { newAmountPaid = it },
                        label = { Text("Amount Paid") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newExpense = Expense(
                                date = newDate,
                                paymentTo = newPaymentTo,
                                paymentFrom = newPaymentFrom,
                                amountPaid = newAmountPaid.toDouble()
                            )
                            dao.insertExpense(newExpense)
                            expenses.value = dao.getAllExpenses()
                        }
                        showDialog = false
                    }
                ) {
                    Text("Add")
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
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AEA8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(expenses.value.size) { index ->
                ExpenseRow(expense = expenses.value[index])
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

    }
}


@Composable
fun ExpenseRow(expense: Expense) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = expense.date,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${expense.paymentTo} from ${expense.paymentFrom}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "₹${expense.amountPaid}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}