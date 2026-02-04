package com.example.androidbootcampiwatepref

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidbootcampiwatepref.ui.CartScreen
import com.example.androidbootcampiwatepref.ui.ClothApp
import com.example.androidbootcampiwatepref.ui.ClothDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // ▶ 一覧画面
        composable("home") {
            ClothApp(navController)
        }

        // ▶ 詳細画面
        composable(
            route = "detail/{clothId}",
            arguments = listOf(
                navArgument("clothId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val clothId = backStackEntry.arguments?.getString("clothId")
            ClothDetailScreen(navController, clothId)
        }

        composable("cart"){
            CartScreen(
                onBack = { navController. popBackStack() }
            )
        }
    }
}
