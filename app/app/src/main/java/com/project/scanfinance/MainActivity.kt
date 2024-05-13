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
    Expense(0,"2024-05-07", "Jane Doe", "Alice", 10.0),
)