package com.example.zguba.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zguba.ui.screens.CarSwipeScreen
import com.example.zguba.ui.screens.LoginScreen
import com.example.zguba.ui.screens.RegisterScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CarSwipe : Screen("car_swipe")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    currentUserId: Long?,
    userRepository: com.example.zguba.repository.UserRepository,
    onLogout: () -> Unit,
    loginError: String?,
    registerError: String?
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLogin = onLogin,
                errorMessage = loginError
            )
            
            // Navigate when user logs in successfully
            LaunchedEffect(currentUserId) {
                if (currentUserId != null) {
                    navController.navigate(Screen.CarSwipe.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onRegister = onRegister,
                errorMessage = registerError
            )
            
            // Navigate when user registers successfully
            LaunchedEffect(currentUserId) {
                if (currentUserId != null) {
                    navController.navigate(Screen.CarSwipe.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            }
        }
        
        composable(Screen.CarSwipe.route) {
            if (currentUserId != null) {
                CarSwipeScreen(
                    userId = currentUserId,
                    userRepository = userRepository,
                    onLogout = {
                        onLogout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

