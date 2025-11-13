package com.example.zguba.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.zguba.data.CarRepository
import com.example.zguba.model.Car
import com.example.zguba.ui.components.AddCarDialog
import com.example.zguba.ui.components.SwipeableCard
import kotlinx.coroutines.launch

/**
 * Main screen for the car swiping functionality.
 * 
 * This screen manages:
 * - Loading cars from the database
 * - Displaying cars in a swipeable card stack
 * - Handling like/dislike actions
 * - Allowing users to add their own cars
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarSwipeScreen() {
    // Get the Android context to access the database
    val context = LocalContext.current
    
    // State management: Store the list of cars loaded from database
    var cars by remember { mutableStateOf<List<Car>>(emptyList()) }
    
    // State management: Track which car is currently being displayed (index in the list)
    var currentIndex by remember { mutableStateOf(0) }
    
    // State management: Keep track of cars the user has liked during this session
    var likedCars by remember { mutableStateOf<List<Car>>(emptyList()) }
    
    // State management: Show loading spinner while fetching cars from database
    var isLoading by remember { mutableStateOf(true) }
    
    // State management: Store any error messages if loading fails
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // State management: Key used to trigger reload of cars (incremented to force refresh)
    var reloadKey by remember { mutableStateOf(0) }
    
    // State management: Control whether the "Add Car" dialog is visible
    var showAddDialog by remember { mutableStateOf(false) }
    
    // State management: Track if a car is currently being saved to database
    var isSavingCar by remember { mutableStateOf(false) }
    
    // State management: Store any errors that occur when saving a new car
    var addCarError by remember { mutableStateOf<String?>(null) }
    
    // Coroutine scope for running async operations (database calls)
    val scope = rememberCoroutineScope()

    /**
     * LaunchedEffect: Runs whenever reloadKey changes (when user clicks "Start Over" or after adding a car)
     * 
     * Steps:
     * 1. Set loading state to true (show spinner)
     * 2. Clear any previous error messages
     * 3. Try to load cars from database via CarRepository
     * 4. If successful: update cars list, reset index to 0, clear liked cars
     * 5. If failed: store error message to display to user
     * 6. Finally: set loading to false (hide spinner)
     */
    LaunchedEffect(reloadKey) {
        isLoading = true
        errorMessage = null
        try {
            // Load cars from database (this is a suspend function, so it runs on background thread)
            cars = CarRepository.getCars(context)
            // Reset to first car in the list
            currentIndex = 0
            // Clear the liked cars list for this session
            likedCars = emptyList()
        } catch (exception: Exception) {
            // If loading fails, store the error message
            errorMessage = exception.message ?: "Failed to load cars"
        } finally {
            // Always hide loading spinner, whether success or failure
            isLoading = false
        }
    }

    // Get the current car being displayed, or null if we've swiped through all cars
    val currentCar = if (currentIndex < cars.size) cars[currentIndex] else null
    
    /**
     * Scaffold: Main layout structure with top bar, bottom bar, and content area
     */
    Scaffold(
        // Top bar: Display app title
        topBar = {
            TopAppBar(
                title = { Text("Car Swiper") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // Bottom bar: Contains like/dislike buttons
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
                    /**
                     * Dislike button: Red button with X icon
                     * When clicked: Move to next car without adding to liked list
                     */
                    FloatingActionButton(
                        onClick = {
                            // Only increment if there are more cars
                            if (currentIndex < cars.size) {
                                currentIndex++ // Move to next car
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
                    
                    /**
                     * Like button: Green button with heart icon
                     * When clicked: Add current car to liked list and move to next car
                     */
                    FloatingActionButton(
                        onClick = {
                            if (currentIndex < cars.size) {
                                // Add current car to the liked cars list
                                likedCars = likedCars + cars[currentIndex]
                                // Move to next car
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
        /**
         * Main content area: Displays different UI based on current state
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Account for top/bottom bars
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                /**
                 * State 1: Loading - Show spinner while fetching cars from database
                 */
                isLoading -> {
                    CircularProgressIndicator()
                }
                
                /**
                 * State 2: Error - Show error message with retry button
                 */
                errorMessage != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Display the error message
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        // Retry button: Increment reloadKey to trigger LaunchedEffect again
                        Button(onClick = { reloadKey += 1 }) {
                            Text("Retry")
                        }
                    }
                }
                
                /**
                 * State 3: Has cars to display - Show swipeable card stack
                 */
                currentCar != null -> {
                    /**
                     * Card stacking effect: Show next 2 cards behind current card for depth
                     * 
                     * Card 2 (next card): Slightly offset, smaller, semi-transparent
                     */
                    if (currentIndex + 1 < cars.size) {
                        SwipeableCard(
                            car = cars[currentIndex + 1], // Next car in list
                            onSwipeLeft = {}, // Empty callbacks - this card is not draggable
                            onSwipeRight = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = 8.dp) // Slightly below
                                .scale(0.95f) // Slightly smaller
                                .alpha(0.7f) // Semi-transparent
                        )
                    }
                    
                    /**
                     * Card 3 (card after next): Even more offset, smaller, more transparent
                     */
                    if (currentIndex + 2 < cars.size) {
                        SwipeableCard(
                            car = cars[currentIndex + 2], // Car after next
                            onSwipeLeft = {}, // Empty callbacks - not draggable
                            onSwipeRight = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = 16.dp) // More offset
                                .scale(0.9f) // Smaller
                                .alpha(0.5f) // More transparent
                        )
                    }
                    
                    /**
                     * Current card: Top card that user can swipe
                     * This is the only draggable card - others are just for visual effect
                     */
                    SwipeableCard(
                        car = currentCar, // Current car being displayed
                        onSwipeLeft = {
                            // User swiped left (dislike): Move to next car
                            currentIndex++
                        },
                        onSwipeRight = {
                            // User swiped right (like): Add to liked list and move to next
                            likedCars = likedCars + currentCar
                            currentIndex++
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                /**
                 * State 4: No more cars - Show end screen with options
                 */
                else -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Display "No more cars" message
                        Text(
                            text = "No more cars!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Show count of liked cars
                        Text(
                            text = "You liked ${likedCars.size} car(s)",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        // Start Over button: Reload all cars and start from beginning
                        Button(onClick = { reloadKey += 1 }) {
                            Text("Start Over")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Add Your Car button: Open dialog to add a new car
                        Button(onClick = {
                            showAddDialog = true // Show the add car dialog
                            addCarError = null // Clear any previous errors
                        }) {
                            Text("Add Your Car")
                        }
                    }
                }
            }
        }
    }

    /**
     * Add Car Dialog: Displayed when user wants to add their own car
     * 
     * This dialog:
     * 1. Shows form fields for car information
     * 2. Validates input
     * 3. Saves car to database when submitted
     * 4. Reloads the car list to include the new car
     */
    if (showAddDialog) {
        AddCarDialog(
            // Called when user dismisses dialog (clicks Cancel or outside)
            onDismiss = {
                // Only allow dismissal if not currently saving
                if (!isSavingCar) {
                    showAddDialog = false
                    addCarError = null
                }
            },
            // Called when user submits the form
            onAdd = { car ->
                // Launch coroutine to save car to database (async operation)
                scope.launch {
                    isSavingCar = true // Show "Saving..." state
                    addCarError = null // Clear previous errors
                    try {
                        // Save car to database via repository
                        CarRepository.addCar(context, car)
                        // Close dialog
                        showAddDialog = false
                        // Trigger reload to refresh car list (includes new car)
                        reloadKey += 1
                    } catch (exception: Exception) {
                        // If save fails, store error message to display in dialog
                        addCarError = exception.message ?: "Failed to save car"
                    } finally {
                        // Always reset saving state
                        isSavingCar = false
                    }
                }
            },
            // Pass saving state to dialog (disables button while saving)
            isSaving = isSavingCar,
            // Pass error message to dialog (displays if save fails)
            errorMessage = addCarError
        )
    }
}
