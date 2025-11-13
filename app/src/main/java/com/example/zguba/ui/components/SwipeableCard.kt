package com.example.zguba.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.zguba.model.Car
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * SwipeableCard: A card component that displays car information and can be swiped left/right
 * 
 * Features:
 * - Displays car image, make, model, year, price, and description
 * - Responds to drag gestures (swipe left = dislike, swipe right = like)
 * - Animates rotation, scale, and opacity based on swipe distance
 * - Shows like/dislike overlay icons when swiping
 * - Calls callbacks when swipe threshold is reached
 * 
 * @param car: The car data to display
 * @param onSwipeLeft: Callback called when user swipes left (dislike)
 * @param onSwipeRight: Callback called when user swipes right (like)
 * @param modifier: Additional modifiers for styling/positioning
 */
@Composable
fun SwipeableCard(
    car: Car,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * State Management: Track card position and swipe state
     * 
     * Keyed to car.id: When car changes, state resets to 0
     * This ensures each new card starts at center position
     */
    // Horizontal offset: Positive = right, Negative = left
    var offsetX by remember(car.id) { mutableStateOf(0f) }
    // Vertical offset: Allows slight vertical movement during swipe
    var offsetY by remember(car.id) { mutableStateOf(0f) }
    // Track if user is currently dragging the card
    var isSwiping by remember(car.id) { mutableStateOf(false) }
    
    // Coroutine scope for running animations and delays
    val scope = rememberCoroutineScope()
    
    /**
     * Animated Properties: These values animate smoothly as offsetX changes
     * 
     * Rotation: Card tilts based on horizontal position
     * - Swipe right = positive rotation (clockwise)
     * - Swipe left = negative rotation (counter-clockwise)
     * - Formula: offsetX / 10f means 100px swipe = 10 degrees rotation
     */
    val rotation by animateFloatAsState(
        targetValue = offsetX / 10f, // Divide by 10 to make rotation subtle
        animationSpec = tween(300), // 300ms animation duration
        label = "rotation"
    )
    
    /**
     * Alpha (Opacity): Card fades out as it moves away from center
     * - At center (offsetX = 0): alpha = 1.0 (fully visible)
     * - At 2000px away: alpha = 0 (fully transparent)
     * - Formula: 1f - abs(offsetX) / 2000f
     */
    val alpha by animateFloatAsState(
        targetValue = 1f - abs(offsetX) / 2000f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    /**
     * Scale: Card shrinks slightly as it moves away
     * - At center: scale = 1.0 (full size)
     * - At 3000px away: scale = 0 (invisible)
     * - Formula: 1f - abs(offsetX) / 3000f
     */
    val scale by animateFloatAsState(
        targetValue = 1f - abs(offsetX) / 3000f,
        animationSpec = tween(300),
        label = "scale"
    )
    
    /**
     * Overlay Visibility: Show like/dislike icons when swiping
     * - Swipe right > 50px: Show green heart (like)
     * - Swipe left < -50px: Show red X (dislike)
     */
    val showLike = offsetX > 50
    val showDislike = offsetX < -50
    
    /**
     * Main Container: Box that applies transformations (offset, rotation, scale, alpha)
     */
    Box(
        modifier = modifier
            // Apply horizontal and vertical offset based on drag position
            .offset {
                IntOffset(
                    offsetX.roundToInt(), // Convert float to int pixels
                    offsetY.roundToInt()
                )
            }
            // Rotate card based on horizontal position
            .rotate(rotation)
            // Fade out as card moves away
            .alpha(alpha)
            // Shrink as card moves away
            .scale(scale)
            // Ensure card appears on top of background cards
            .zIndex(1f)
    ) {
        /**
         * Card Component: Material Design card containing car content
         */
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                /**
                 * Gesture Detection: Detect drag gestures on the card
                 * 
                 * Keyed to car.id: Resets gesture detection when car changes
                 * This ensures each new card has fresh gesture state
                 */
                .pointerInput(car.id) {
                    detectDragGestures(
                        /**
                         * onDragStart: Called when user starts dragging
                         * Set isSwiping flag to true
                         */
                        onDragStart = {
                            isSwiping = true
                        },
                        /**
                         * onDragEnd: Called when user releases finger
                         * 
                         * Steps:
                         * 1. Set isSwiping to false
                         * 2. Check if swipe distance exceeds threshold (200px)
                         * 3. If threshold exceeded:
                         *    - Animate card off screen (2000px)
                         *    - Wait 300ms for animation
                         *    - Call appropriate callback (onSwipeLeft/onSwipeRight)
                         * 4. If threshold not met:
                         *    - Snap card back to center (offsetX = 0)
                         */
                        onDragEnd = {
                            isSwiping = false
                            val threshold = 200f // Minimum swipe distance to trigger action
                            when {
                                // Swiped right (like): Move card off screen to the right
                                offsetX > threshold -> {
                                    // Animate off screen to the right
                                    offsetX = 2000f
                                    offsetY = 0f
                                    // Callback after animation completes
                                    scope.launch {
                                        delay(300) // Wait for animation
                                        onSwipeRight() // Trigger like action
                                    }
                                }
                                // Swiped left (dislike): Move card off screen to the left
                                offsetX < -threshold -> {
                                    // Animate off screen to the left
                                    offsetX = -2000f
                                    offsetY = 0f
                                    // Callback after animation completes
                                    scope.launch {
                                        delay(300) // Wait for animation
                                        onSwipeLeft() // Trigger dislike action
                                    }
                                }
                                // Swipe distance too small: Snap back to center
                                else -> {
                                    // Snap back to center
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }
                        }
                    ) { change, dragAmount ->
                        /**
                         * Drag Handler: Called continuously while user is dragging
                         * 
                         * Updates offsetX and offsetY based on drag distance
                         * - dragAmount.x: Horizontal movement (positive = right, negative = left)
                         * - dragAmount.y: Vertical movement (reduced to 30% for less vertical drift)
                         */
                        offsetX += dragAmount.x // Accumulate horizontal movement
                        offsetY += dragAmount.y * 0.3f // Less vertical movement (30% of actual)
                    }
                },
            shape = RoundedCornerShape(24.dp), // Rounded corners
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Shadow effect
        ) {
            /**
             * Card Content: Box containing all car information
             */
            Box(modifier = Modifier.fillMaxSize()) {
                /**
                 * Car Image: Load and display car image from URL
                 * Uses Coil library for efficient image loading and caching
                 */
                AsyncImage(
                    model = car.imageUrl, // Image URL from car data
                    contentDescription = "${car.make} ${car.model}", // Accessibility description
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Crop image to fill card
                )
                
                /**
                 * Gradient Overlay: Dark gradient at bottom for text readability
                 * Creates fade effect from transparent at top to dark at bottom
                 */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Height of gradient
                        .align(Alignment.BottomCenter) // Position at bottom
                        .background(
                            // Vertical gradient: transparent to dark
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent, // Top: transparent
                                    Color.Black.copy(alpha = 0.7f) // Bottom: 70% opacity black
                                )
                            )
                        )
                )
                
                /**
                 * Car Information: Text overlay showing car details
                 * Positioned at bottom left over the gradient
                 */
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart) // Bottom left position
                        .padding(24.dp) // Padding from edges
                ) {
                    // Car title: Year, Make, Model (e.g., "2024 Tesla Model S")
                    Text(
                        text = "${car.year} ${car.make} ${car.model}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Car price: Formatted with commas (e.g., "$89,990")
                    Text(
                        text = "$${String.format("%,d", car.price)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Description: Only show if car has a description
                    if (car.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = car.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f) // Slightly transparent
                        )
                    }
                }
                
                /**
                 * Like Overlay: Green overlay with heart icon
                 * Shown when user swipes right (offsetX > 50px)
                 */
                if (showLike) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green.copy(alpha = 0.3f)), // 30% opacity green
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = Color.Green,
                            modifier = Modifier.size(120.dp) // Large icon
                        )
                    }
                }
                
                /**
                 * Dislike Overlay: Red overlay with X icon
                 * Shown when user swipes left (offsetX < -50px)
                 */
                if (showDislike) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red.copy(alpha = 0.3f)), // 30% opacity red
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dislike",
                            tint = Color.Red,
                            modifier = Modifier.size(120.dp) // Large icon
                        )
                    }
                }
            }
        }
    }
}
