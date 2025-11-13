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

/**
 * AddCarDialog: Dialog component for adding a new car to the database
 * 
 * Features:
 * - Form fields for car information (make, model, year, price, image URL, description)
 * - Real-time validation of required fields
 * - Submit button enabled only when all required fields are valid
 * - Shows loading state while saving
 * - Displays error messages if save fails
 * 
 * @param onDismiss: Callback when user cancels or closes dialog
 * @param onAdd: Callback when user submits form (passes the new Car object)
 * @param isSaving: Whether car is currently being saved (disables buttons)
 * @param errorMessage: Error message to display if save fails
 */
@Composable
fun AddCarDialog(
    onDismiss: () -> Unit,
    onAdd: (Car) -> Unit,
    isSaving: Boolean = false,
    errorMessage: String? = null
) {
    /**
     * Form State: Track values entered in each text field
     * 
     * Each field uses mutableStateOf to store user input
     * State updates trigger recomposition, updating the UI
     */
    var make by remember { mutableStateOf("") } // Car manufacturer (e.g., "Tesla")
    var model by remember { mutableStateOf("") } // Car model (e.g., "Model S")
    var year by remember { mutableStateOf("") } // Manufacturing year (e.g., "2024")
    var price by remember { mutableStateOf("") } // Car price (e.g., "89990")
    var imageUrl by remember { mutableStateOf("") } // URL to car image
    var description by remember { mutableStateOf("") } // Optional description
    
    /**
     * Validation: Check if form can be submitted
     * 
     * Uses remember with keys: Recomputes whenever any field changes
     * 
     * Validation rules:
     * - Make: Must not be blank
     * - Model: Must not be blank
     * - Year: Must be a valid integer (toIntOrNull() != null)
     * - Price: Must be a valid integer (toIntOrNull() != null)
     * - Image URL: Must not be blank
     * - Description: Optional (not validated)
     * 
     * Button is enabled only when canSubmit = true AND not currently saving
     */
    val canSubmit = remember(make, model, year, price, imageUrl) {
        make.isNotBlank() &&
        model.isNotBlank() &&
        year.toIntOrNull() != null && // Valid integer
        price.toIntOrNull() != null && // Valid integer
        imageUrl.isNotBlank()
    }

    /**
     * AlertDialog: Material Design dialog component
     */
    AlertDialog(
        // Called when user clicks outside dialog or presses back
        onDismissRequest = onDismiss,
        // Confirm button: Submit the form
        confirmButton = {
            Button(
                onClick = {
                    /**
                     * Submit Handler: Called when user clicks "Add" button
                     * 
                     * Steps:
                     * 1. Parse year and price (use 0 as default if invalid)
                     * 2. Create Car object with:
                     *    - Unique ID (UUID)
                     *    - Trimmed text fields (remove leading/trailing spaces)
                     *    - Parsed numeric values
                     * 3. Call onAdd callback with the new Car object
                     *    - Parent component (CarSwipeScreen) handles saving to database
                     */
                    val safeYear = year.toIntOrNull() ?: 0 // Parse year, default to 0
                    val safePrice = price.toIntOrNull() ?: 0 // Parse price, default to 0
                    val carToAdd = Car(
                        id = UUID.randomUUID().toString(), // Generate unique ID
                        make = make.trim(), // Remove whitespace
                        model = model.trim(),
                        year = safeYear,
                        price = safePrice,
                        imageUrl = imageUrl.trim(),
                        description = description.trim()
                    )
                    onAdd(carToAdd) // Pass car to parent component
                },
                // Button enabled only when form is valid and not saving
                enabled = canSubmit && !isSaving
            ) {
                // Show "Saving..." text while saving, "Add" otherwise
                Text(text = if (isSaving) "Saving..." else "Add")
            }
        },
        // Dismiss button: Cancel and close dialog
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                // Disable cancel button while saving (prevent closing during save)
                enabled = !isSaving
            ) {
                Text("Cancel")
            }
        },
        // Dialog title
        title = {
            Text("Add Your Car")
        },
        // Dialog content: Form fields
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                /**
                 * Error Message: Display if save failed
                 * Shown at top of form in red color
                 */
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
                
                /**
                 * Make Field: Car manufacturer
                 * Required field, single line text input
                 */
                OutlinedTextField(
                    value = make, // Current value
                    onValueChange = { make = it }, // Update state when user types
                    label = { Text("Make") }, // Placeholder label
                    singleLine = true // Single line input
                )
                
                /**
                 * Model Field: Car model
                 * Required field, single line text input
                 */
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model") },
                    singleLine = true
                )
                
                /**
                 * Year Field: Manufacturing year
                 * Required field, numeric keyboard only
                 */
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year (e.g. 2024)") },
                    singleLine = true,
                    // Show numeric keyboard on mobile devices
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                
                /**
                 * Price Field: Car price
                 * Required field, numeric keyboard only
                 */
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    singleLine = true,
                    // Show numeric keyboard on mobile devices
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                
                /**
                 * Image URL Field: URL to car image
                 * Required field, single line text input
                 */
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    singleLine = true
                )
                
                /**
                 * Description Field: Optional car description
                 * Multi-line text input (2-4 lines)
                 */
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    singleLine = false, // Allow multiple lines
                    minLines = 2, // Minimum 2 lines
                    maxLines = 4 // Maximum 4 lines
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    )
}
