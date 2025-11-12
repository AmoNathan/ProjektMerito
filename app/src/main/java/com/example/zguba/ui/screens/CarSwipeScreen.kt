package com.example.zguba.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.zguba.data.CarRepository
import com.example.zguba.model.Car
import com.example.zguba.repository.UserRepository
import com.example.zguba.ui.components.SwipeableCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarSwipeScreen(
    userId: Long,
    userRepository: UserRepository,
    onLogout: () -> Unit
) {
    var cars by remember { mutableStateOf(CarRepository.getCars()) }
    var currentIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    
    val likedCarCount by userRepository.getLikedCarCount(userId).collectAsState(initial = 0)
    
    val currentCar = if (currentIndex < cars.size) cars[currentIndex] else null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Swiper") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dislike button
                    FloatingActionButton(
                        onClick = {
                            if (currentIndex < cars.size) {
                                currentIndex++
                            }
                        },
                        containerColor = Color.Red,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dislike",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Like button
                    FloatingActionButton(
                        onClick = {
                            if (currentIndex < cars.size) {
                                val car = cars[currentIndex]
                                scope.launch {
                                    userRepository.likeCar(userId, car)
                                }
                                currentIndex++
                            }
                        },
                        containerColor = Color.Green,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentCar != null) {
                // Show next 2 cards in stack for depth effect
                if (currentIndex + 1 < cars.size) {
                    SwipeableCard(
                        car = cars[currentIndex + 1],
                        onSwipeLeft = {},
                        onSwipeRight = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = 8.dp)
                            .scale(0.95f)
                            .alpha(0.7f)
                    )
                }
                
                if (currentIndex + 2 < cars.size) {
                    SwipeableCard(
                        car = cars[currentIndex + 2],
                        onSwipeLeft = {},
                        onSwipeRight = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = 16.dp)
                            .scale(0.9f)
                            .alpha(0.5f)
                    )
                }
                
                // Current card on top
                SwipeableCard(
                    car = currentCar,
                    onSwipeLeft = {
                        currentIndex++
                    },
                    onSwipeRight = {
                        scope.launch {
                            userRepository.likeCar(userId, currentCar)
                        }
                        currentIndex++
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // No more cars
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No more cars!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You liked $likedCarCount car(s)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            cars = CarRepository.getCars()
                            currentIndex = 0
                        }
                    ) {
                        Text("Start Over")
                    }
                }
            }
        }
    }
}

