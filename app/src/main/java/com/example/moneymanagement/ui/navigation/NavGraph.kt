package com.example.moneymanagement.ui.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moneymanagement.R
import com.example.moneymanagement.ui.screens.selectscreen.SelectScreen
import com.example.moneymanagement.ui.screens.login.LoginScreen
import com.example.moneymanagement.ui.screens.mainscreen.MainScreen
import com.example.moneymanagement.ui.screens.typescreen.TypeScreen


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(
            Screen.Login.route
        ) {
            LoginScreen(onLoginSuccess = {
                // navigate to the main messages screen
                navController.navigate(Screen.Select.route)
            })
        }
        composable(
            Screen.Main.route,
            arguments = listOf(
                navArgument("periodYear") { type = NavType.StringType },
                navArgument("periodMonth") { type = NavType.StringType }
            )
        ) {
            val year = it.arguments?.getString("periodYear")
            val month = it.arguments?.getString("periodMonth")

            if (year != null && month != null) {
                MainScreen(
                    periodYear = year,
                    periodMonth = month,
                    navController = navController,
                    onTypeSelected = { sameYear, sameMonth, category ->
                        navController.navigate(
                            context.getString(
                                R.string.main_nav_text,
                                sameYear,
                                sameMonth,
                                category
                            )
                        )
                    })
            }
        }
        composable(
            Screen.Type.route,
            arguments = listOf(
                navArgument("periodYear") { type = NavType.StringType },
                navArgument("periodMonth") { type = NavType.StringType },
                navArgument("selectedCategory") { type = NavType.StringType },
            )
        ) {
            val year = it.arguments?.getString("periodYear")
            val month = it.arguments?.getString("periodMonth")
            val category = it.arguments?.getString("selectedCategory")

            if (year != null && month != null && category != null) {
                TypeScreen(
                    periodYear = year,
                    periodMonth = month,
                    categoryChosen = category,
                    navController = navController
                )
            }
        }
        composable(Screen.Select.route) {
            SelectScreen(
                onPeriodSelected = { year, month ->
                    navController.navigate(context.getString(R.string.select_nav_text, year, month))
                }
            )
        }
    }
}