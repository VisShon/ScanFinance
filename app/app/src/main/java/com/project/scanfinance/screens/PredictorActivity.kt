package com.project.scanfinance.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import com.jaikeerthick.composable_graphs.composables.bar.BarGraph
import com.jaikeerthick.composable_graphs.composables.bar.model.BarData

import androidx.compose.ui.unit.dp
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphColors
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphFillType
import com.jaikeerthick.composable_graphs.composables.bar.style.BarGraphStyle
import com.project.scanfinance.components.BudgetInput
import com.project.scanfinance.components.BudgetPredictor
import com.project.scanfinance.database.Expense


@Composable
fun PredictorActivity (expenses:List<Expense>) {
    val groupedExpenses = expenses.groupBy { it.date }
        .mapValues { (_, expenses) ->
            expenses.sumOf { it.amountPaid }
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

        BudgetInput()

        BarGraph(
            data = graphData,
            style = BarGraphStyle(
                colors = BarGraphColors(
                    fillType = BarGraphFillType.Gradient(
                        brush = Brush.verticalGradient(listOf(Color(0xFF39AEA8),Color.White))
                    )
                )
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        BudgetPredictor()
    }
}


