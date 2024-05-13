package com.project.scanfinance.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun Navbar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Info, contentDescription = "Predictor") },
            label = { Text("Predictor") },
            selected = false,
            onClick = { navController.navigate("predictor") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.AddCircle, contentDescription = "Scanner") },
            label = { Text("Scanner") },
            selected = false,
            onClick = { navController.navigate("scanner") }
        )
    }
}
