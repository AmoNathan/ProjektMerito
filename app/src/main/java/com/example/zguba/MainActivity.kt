package com.example.zguba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.zguba.database.DatabaseModule
import com.example.zguba.navigation.NavGraph
import com.example.zguba.navigation.Screen
import com.example.zguba.repository.UserRepository
import com.example.zguba.ui.theme.ZgubaTheme
import com.example.zguba.utils.PreferencesManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
        val database = DatabaseModule.getDatabase(this)
        val userRepository = UserRepository(database)
        val preferencesManager = PreferencesManager(this)
        
        setContent {
            ZgubaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var currentUserId by remember { mutableStateOf<Long?>(preferencesManager.getUserId()) }
                    var loginError by remember { mutableStateOf<String?>(null) }
                    var registerError by remember { mutableStateOf<String?>(null) }
                    val scope = rememberCoroutineScope()
                    
                    // Determine start destination
                    val startDestination = if (currentUserId != null) {
                        Screen.CarSwipe.route
                    } else {
                        Screen.Login.route
                    }
                    
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        currentUserId = currentUserId,
                        userRepository = userRepository,
                        onLogin = { username, password ->
                            scope.launch {
                                loginError = null
                                userRepository.login(username, password)
                                    .onSuccess { user ->
                                        currentUserId = user.id
                                        preferencesManager.saveUserId(user.id)
                                        loginError = null
                                    }
                                    .onFailure { exception ->
                                        loginError = exception.message
                                    }
                            }
                        },
                        onRegister = { username, email, password ->
                            scope.launch {
                                registerError = null
                                userRepository.createUser(username, email, password)
                                    .onSuccess { userId ->
                                        currentUserId = userId
                                        preferencesManager.saveUserId(userId)
                                        registerError = null
                                    }
                                    .onFailure { exception ->
                                        registerError = exception.message
                                    }
                            }
                        },
                        loginError = loginError,
                        registerError = registerError
                    )
                }
            }
        }
    }
}