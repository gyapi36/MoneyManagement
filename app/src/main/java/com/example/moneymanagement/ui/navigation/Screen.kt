package com.example.moneymanagement.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main/{periodYear}/{periodMonth}")
    object Type : Screen("type/{periodYear}/{periodMonth}/{selectedCategory}")
    object Select : Screen("select")
}