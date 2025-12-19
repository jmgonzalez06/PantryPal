package com.jose.pantrypal.inventory

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Composable
fun AddItemScreen(
    viewModel: InventoryViewModel,
    onItemAdded: () -> Unit
) {
    var dateError by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var expiryDate by remember { mutableStateOf<Timestamp?>(null) }
    var zoneId by remember { mutableStateOf("pantry") }


    val context = LocalContext.current
    val today = LocalDate.now()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val selectedDate = LocalDate.of(year, month + 1, day)
            val today = LocalDate.now()

            if (selectedDate.isBefore(today)) {
                dateError = "Expiry date cannot be in the past"
                expiryDate = null
            } else {
                dateError = null
                expiryDate = Timestamp(
                    Date(
                        selectedDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                    )
                )
            }
        },
        today.year,
        today.monthValue - 1,
        today.dayOfMonth
    )

    Column(modifier = Modifier.padding(16.dp)) {

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
                text = expiryDate?.toDate()?.let { date ->
                    val localDate = date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    "Expiry: $localDate"
                } ?: "Pick Expiry Date"
            )
        }
        dateError?.let {
            Spacer(Modifier.height(4.dp))
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.height(8.dp))

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

        Button(
            onClick = {
                viewModel.addItem(
                    name = name,
                    expiryDate = expiryDate,
                    quantity = quantity.toIntOrNull() ?: 1,
                    zoneId = zoneId
                )
                onItemAdded()
            },
            enabled = expiryDate != null && name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Item")
        }
    }
}
