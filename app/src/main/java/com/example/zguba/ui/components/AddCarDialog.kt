package com.example.zguba.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.zguba.model.Car
import java.util.UUID

@Composable
fun AddCarDialog(
    onDismiss: () -> Unit,
    onAdd: (Car) -> Unit,
    isSaving: Boolean = false,
    errorMessage: String? = null
) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    val canSubmit = remember(make, model, year, price, imageUrl) {
        make.isNotBlank() &&
        model.isNotBlank() &&
        year.toIntOrNull() != null &&
        price.toIntOrNull() != null &&
        imageUrl.isNotBlank()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val safeYear = year.toIntOrNull() ?: 0
                    val safePrice = price.toIntOrNull() ?: 0
                    val carToAdd = Car(
                        id = UUID.randomUUID().toString(),
                        make = make.trim(),
                        model = model.trim(),
                        year = safeYear,
                        price = safePrice,
                        imageUrl = imageUrl.trim(),
                        description = description.trim()
                    )
                    onAdd(carToAdd)
                },
                enabled = canSubmit && !isSaving
            ) {
                Text(text = if (isSaving) "Saving..." else "Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text("Add Your Car")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
                OutlinedTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = { Text("Make") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year (e.g. 2024)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    )
}

