package com.project.scanfinance

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.scanfinance.ui.theme.ScanFinanceTheme
import com.project.scanfinance.components.Navbar
import com.project.scanfinance.components.SplashScreen
import com.project.scanfinance.database.ExpenseDatabase

import com.project.scanfinance.screens.HomeActivity
import com.project.scanfinance.screens.PredictorActivity
import com.project.scanfinance.screens.ScannerActivity


class MainActivity : ComponentActivity() {

    private val db by lazy { ExpenseDatabase.getDatabase(this) }
    private val expenseDao by lazy { db.expenseDao() }

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
                        NavHost(navController, startDestination = "home") {
                            composable("home") { HomeActivity(navController,expenseDao) }
                            composable("predictor") { PredictorActivity(expenseDao) }
                            composable("scanner") { ScannerActivity(expenseDao) }
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

