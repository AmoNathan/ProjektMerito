package com.example.zguba.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeableCard(
    car: Car,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    key(car.id) {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        var isSwiping by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        val rotation by animateFloatAsState(
            targetValue = offsetX / 10f,
            animationSpec = tween(300),
            label = "rotation"
        )

        val alpha by animateFloatAsState(
            targetValue = 1f - abs(offsetX) / 2000f,
            animationSpec = tween(300),
            label = "alpha"
        )

        val scale by animateFloatAsState(
            targetValue = 1f - abs(offsetX) / 3000f,
            animationSpec = tween(300),
            label = "scale"
        )

        // Show like/dislike overlay
        val showLike = offsetX > 50
        val showDislike = offsetX < -50

        Box(
            modifier = modifier
                .offset {
                    IntOffset(
                        offsetX.roundToInt(),
                        offsetY.roundToInt()
                    )
                }
                .rotate(rotation)
                .alpha(alpha)
                .scale(scale)
                .zIndex(1f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isSwiping = true
                            },
                            onDragEnd = {
                                isSwiping = false
                                val threshold = 200f
                                when {
                                    offsetX > threshold -> {
                                        // Animate off screen to the right
                                        offsetX = 2000f
                                        offsetY = 0f
                                        // Callback after animation
                                        scope.launch {
                                            delay(300)
                                            onSwipeRight()
                                        }
                                    }
                                    offsetX < -threshold -> {
                                        // Animate off screen to the left
                                        offsetX = -2000f
                                        offsetY = 0f
                                        // Callback after animation
                                        scope.launch {
                                            delay(300)
                                            onSwipeLeft()
                                        }
                                    }
                                    else -> {
                                        // Snap back to center
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                }
                            }
                        ) { _, dragAmount ->
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y * 0.3f // Less vertical movement
                        }
                    },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Car Image
                    AsyncImage(
                        model = car.imageUrl,
                        contentDescription = "${car.make} ${car.model}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Car Info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "${car.year} ${car.make} ${car.model}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${String.format("%,d", car.price)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (car.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = car.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Like overlay
                    if (showLike) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Green.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Like",
                                tint = Color.Green,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }

                    // Dislike overlay
                    if (showDislike) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dislike",
                                tint = Color.Red,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
