package com.project.scanfinance

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.scanfinance.ui.theme.ScanFinanceTheme
import com.project.scanfinance.components.Navbar
import com.project.scanfinance.components.SplashScreen
import com.project.scanfinance.database.Expense

import com.project.scanfinance.screens.HomeActivity
import com.project.scanfinance.screens.PredictorActivity
import com.project.scanfinance.screens.ScannerActivity


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScanFinanceTheme {
                val navController = rememberNavController()
                val showMainScreen = remember { mutableStateOf(false) }

                if (showMainScreen.value) {
                    Scaffold(
                        bottomBar = { Navbar(navController) }
                    ) {
                        NavHost(navController, startDestination = "predictor") {
                            composable("home") { HomeActivity(navController,expenses) }
                            composable("predictor") { PredictorActivity(expenses) }
                            composable("scanner") { ScannerActivity() }
                        }
                    }
                } else {
                    SplashScreen(onAnimationComplete = {
                        showMainScreen.value = true
                    })
                }
            }
        }
    }
}

val expenses = listOf(
    Expense("2024-05-07", "Jane Doe", "Alice", 10.0),
    Expense("2024-05-07", "Jane Doe", "Bob", 15.0),
    Expense("2024-05-07", "Jane Doe", "Charlie", 12.0),
    Expense("2024-05-07", "Jane Doe", "David", 8.0),
    Expense("2024-05-07", "Jane Doe", "Eve", 20.0),
    Expense("2024-05-07", "Jane Doe", "Frank", 18.0),
    Expense("2024-05-07", "Jane Doe", "Grace", 5.0),
    Expense("2024-05-07", "Jane Doe", "Hank", 22.0),
    Expense("2024-05-07", "Jane Doe", "Ivy", 13.0),
    Expense("2024-05-07", "Jane Doe", "John", 19.0),

    Expense("2024-05-08", "Jane Doe", "Alice", 11.0),
    Expense("2024-05-08", "Jane Doe", "Bob", 9.0),
    Expense("2024-05-08", "Jane Doe", "Charlie", 16.0),
    Expense("2024-05-08", "Jane Doe", "David", 14.0),
    Expense("2024-05-08", "Jane Doe", "Eve", 7.0),
    Expense("2024-05-08", "Jane Doe", "Frank", 25.0),
    Expense("2024-05-08", "Jane Doe", "Grace", 12.0),
    Expense("2024-05-08", "Jane Doe", "Hank", 6.0),
    Expense("2024-05-08", "Jane Doe", "Ivy", 21.0),
    Expense("2024-05-08", "Jane Doe", "John", 10.0),

    Expense("2024-05-09", "Jane Doe", "Alice", 8.0),
    Expense("2024-05-09", "Jane Doe", "Bob", 17.0),
    Expense("2024-05-09", "Jane Doe", "Charlie", 19.0),
    Expense("2024-05-09", "Jane Doe", "David", 15.0),
    Expense("2024-05-09", "Jane Doe", "Eve", 22.0),
    Expense("2024-05-09", "Jane Doe", "Frank", 11.0),
    Expense("2024-05-09", "Jane Doe", "Grace", 9.0),
    Expense("2024-05-09", "Jane Doe", "Hank", 20.0),
    Expense("2024-05-09", "Jane Doe", "Ivy", 12.0),
    Expense("2024-05-09", "Jane Doe", "John", 13.0),

    Expense("2024-05-10", "Jane Doe", "Alice", 23.0),
    Expense("2024-05-10", "Jane Doe", "Bob", 18.0),
    Expense("2024-05-10", "Jane Doe", "Charlie", 10.0),
    Expense("2024-05-10", "Jane Doe", "David", 7.0),
    Expense("2024-05-10", "Jane Doe", "Eve", 25.0),
    Expense("2024-05-10", "Jane Doe", "Frank", 14.0),
    Expense("2024-05-10", "Jane Doe", "Grace", 5.0),
    Expense("2024-05-10", "Jane Doe", "Hank", 21.0),
    Expense("2024-05-10", "Jane Doe", "Ivy", 9.0),
    Expense("2024-05-10", "Jane Doe", "John", 12.0),

    Expense("2024-05-11", "Jane Doe", "Alice", 11.0),
    Expense("2024-05-11", "Jane Doe", "Bob", 17.0),
    Expense("2024-05-11", "Jane Doe", "Charlie", 20.0),
    Expense("2024-05-11", "Jane Doe", "David", 13.0),
    Expense("2024-05-11", "Jane Doe", "Eve", 15.0),
    Expense("2024-05-11", "Jane Doe", "Frank", 22.0),
    Expense("2024-05-11", "Jane Doe", "Grace", 8.0),
    Expense("2024-05-11", "Jane Doe", "Hank", 9.0),
    Expense("2024-05-11", "Jane Doe", "Ivy", 24.0),
    Expense("2024-05-11", "Jane Doe", "John", 14.0),

    Expense("2024-05-12", "Jane Doe", "Alice", 19.0),
    Expense("2024-05-12", "Jane Doe", "Bob", 10.0),
    Expense("2024-05-12", "Jane Doe", "Charlie", 16.0),
    Expense("2024-05-12", "Jane Doe", "David", 21.0),
    Expense("2024-05-12", "Jane Doe", "Eve", 12.0),
    Expense("2024-05-12", "Jane Doe", "Frank", 18.0),
    Expense("2024-05-12", "Jane Doe", "Grace", 23.0),
    Expense("2024-05-12", "Jane Doe", "Hank", 7.0),
    Expense("2024-05-12", "Jane Doe", "Ivy", 5.0),
    Expense("2024-05-12", "Jane Doe", "John", 20.0),

    Expense("2024-05-13", "Jane Doe", "Alice", 17.0),
    Expense("2024-05-13", "Jane Doe", "Bob", 8.0),
    Expense("2024-05-13", "Jane Doe", "Charlie", 23.0),
    Expense("2024-05-13", "Jane Doe", "David", 14.0),
    Expense("2024-05-13", "Jane Doe", "Eve", 11.0),
    Expense("2024-05-13", "Jane Doe", "Frank", 22.0),
    Expense("2024-05-13", "Jane Doe", "Grace", 19.0),
    Expense("2024-05-13", "Jane Doe", "Hank", 15.0),
    Expense("2024-05-13", "Jane Doe", "Ivy", 9.0),
    Expense("2024-05-13", "Jane Doe", "John", 10.0),

    Expense("2024-05-14", "Jane Doe", "Alice", 20.0),
    Expense("2024-05-14", "Jane Doe", "Bob", 15.0),
    Expense("2024-05-14", "Jane Doe", "Charlie", 9.0),
    Expense("2024-05-14", "Jane Doe", "David", 22.0),
    Expense("2024-05-14", "Jane Doe", "Eve", 8.0),
    Expense("2024-05-14", "Jane Doe", "Frank", 7.0),
    Expense("2024-05-14", "Jane Doe", "Grace", 16.0),
    Expense("2024-05-14", "Jane Doe", "Hank", 24.0),
    Expense("2024-05-14", "Jane Doe", "Ivy", 12.0),
    Expense("2024-05-14", "Jane Doe", "John", 11.0),

    Expense("2024-05-15", "Jane Doe", "Alice", 13.0),
    Expense("2024-05-15", "Jane Doe", "Bob", 24.0),
    Expense("2024-05-15", "Jane Doe", "Charlie", 17.0),
    Expense("2024-05-15", "Jane Doe", "David", 10.0),
    Expense("2024-05-15", "Jane Doe", "Eve", 9.0),
    Expense("2024-05-15", "Jane Doe", "Frank", 14.0),
    Expense("2024-05-15", "Jane Doe", "Grace", 18.0),
    Expense("2024-05-15", "Jane Doe", "Hank", 15.0),
    Expense("2024-05-15", "Jane Doe", "Ivy", 22.0),
    Expense("2024-05-15", "Jane Doe", "John", 21.0),
)