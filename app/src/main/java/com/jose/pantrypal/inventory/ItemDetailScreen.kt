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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.jose.pantrypal.storage.StorageViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Composable
fun ItemDetailScreen(
    item: Item,
    viewModel: InventoryViewModel,
    onBack: (String?) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var expiryDate by remember { mutableStateOf(item.expiryDate) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val storageViewModel: StorageViewModel = viewModel()
    val storageState by storageViewModel.uiState.collectAsState()
    var zoneId by remember { mutableStateOf("pantry") }

    fun norm(s: String?) = s?.trim()?.lowercase() ?: ""

    LaunchedEffect(item.id) {
        name = item.name
        quantity = item.quantity.toString()
        expiryDate = item.expiryDate
        zoneId = item.zoneId ?: "pantry"
    }


    LaunchedEffect(storageState.zones) {
        if (storageState.isLoading) return@LaunchedEffect

        val current = norm(zoneId)
        val exists = storageState.zones.any { zone ->
            norm(zone.zoneName) == current || norm(zone.id) == current
        }
        if (!exists) {
            zoneId = storageState.zones.firstOrNull()?.zoneName ?: "pantry"
        }
    }


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

            // Zone selector
            Text("Storage Zone")

            if (storageState.isLoading) {
                Text("Loading zones...")
            } else {
                storageState.zones.forEach { zone ->
                    val zoneKey = zone.zoneName.ifBlank { zone.id }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = norm(zoneId) == norm(zoneKey),
                            onClick = { zoneId = zoneKey }
                        )
                        Text(zone.zoneName.ifBlank { zone.id })
                    }
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
                    showDeleteDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Item")
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                    },
                    title = {
                        Text("Delete Item")
                    },
                    text = {
                        Text("Do you really want to delete \"${item.name}\"? This action cannot be undone.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteItem(item.id)
                                onBack(item.name)
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                showDeleteDialog = false
                            }
                        ) {
                            Text("No")
                        }
                    }
                )
            }



        }
    }

}
