package com.jose.pantrypal.inventory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jose.pantrypal.items.Item
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import android.app.DatePickerDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.delay

@Composable
fun ItemDetailScreen(
    item: Item,
    viewModel: InventoryViewModel,
    onBack: (String?) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var expiryDate by remember { mutableStateOf(item.expiryDate) }
    var zoneId by remember { mutableStateOf(item.zoneId ?: "pantry") }

    val context = LocalContext.current
    val today = LocalDate.now()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            expiryDate = Timestamp(
                Date(
                    LocalDate.of(year, month + 1, day)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                )
            )
        },
        today.year,
        today.monthValue - 1,
        today.dayOfMonth
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = expiryDate?.toDate()?.let {
                        val localDate = it.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        "Expiry: $localDate"
                    } ?: "Pick Expiry Date"
                )
            }
            Spacer(Modifier.height(8.dp))

            // Zone selector (simple for now)
            Text("Storage Zone")
            listOf("fridge", "freezer", "pantry").forEach { zone ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = zoneId == zone,
                        onClick = { zoneId = zone }
                    )
                    Text(zone.replaceFirstChar { it.uppercase() })
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val today = LocalDate.now()
                    val expiryLocalDate = expiryDate
                        ?.toDate()
                        ?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()

                    if (expiryLocalDate == null || expiryLocalDate.isBefore(today)) {
                        // Optional: show error text instead of silent failure
                        return@Button
                    }

                    viewModel.updateItem(
                        item.copy(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 1,
                            expiryDate = expiryDate,
                            zoneId = zoneId
                        )
                    )
                    onBack(null)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }


            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.deleteItem(item.id)
                    onBack(item.name) // navigate immediately
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Item")
            }

        }
    }

}
