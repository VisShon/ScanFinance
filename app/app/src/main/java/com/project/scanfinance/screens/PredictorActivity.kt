package com.project.scanfinance.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import com.jaikeerthick.composable_graphs.composables.bar.BarGraph
import com.jaikeerthick.composable_graphs.composables.bar.model.BarData

import androidx.compose.ui.unit.dp
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphColors
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphFillType
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphStyle
import com.project.scanfinance.components.BudgetPredictor
import com.project.scanfinance.database.Expense
import com.project.scanfinance.database.ExpenseDAO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.safeToDouble(): Double {
    return try {
        this.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

@Composable
fun PredictorActivity (dao: ExpenseDAO) {

    val expenses = remember { mutableStateOf<List<Expense>>(emptyList()) }
    val estimatedBudget = calculateEstimatedBudget(expenses.value)

    LaunchedEffect(expenses) {
        expenses.value = dao.getAllExpenses()
    }

    val groupedExpenses = expenses.value.groupBy { it.date }
        .mapValues { (_, expenses) ->
            expenses.sumOf { it.amountPaid.toString().safeToDouble()}
        }

    val graphData = groupedExpenses.map { (date, total) ->
        BarData(x = date, y = total)
    }

    Column(
        Modifier.padding(
        horizontal = 10.dp,
        vertical = 30.dp
    )){

        Text(
            text = "Budget Prediction",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (graphData.isNotEmpty()) {
            BarGraph(
                data = graphData,
                style = BarGraphStyle(
                    colors = BarGraphColors(
                        fillType = BarGraphFillType.Gradient(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF39AEA8), Color.White)
                            )
                        )
                    )
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        } else {
            Text(
                text = "No Expenses",
            )
        }

        BudgetPredictor(estimatedBudget)
    }
}

fun calculateEstimatedBudget(expenses: List<Expense>): Double {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val currentDate = LocalDate.now()
    val dateFrom30DaysAgo = currentDate.minusDays(30)

    val last30DaysExpenses = expenses.filter {
        LocalDate.parse(it.date, formatter).isAfter(dateFrom30DaysAgo)
    }

    val totalSpentIn30Days = last30DaysExpenses.sumOf { it.amountPaid }
    val averageBudget = if (last30DaysExpenses.isNotEmpty()) {
        totalSpentIn30Days / last30DaysExpenses.size
    } else {
        0.0
    }

    return averageBudget
}


