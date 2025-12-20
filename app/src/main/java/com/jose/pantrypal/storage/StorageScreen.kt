package com.jose.pantrypal.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
) {
    val storageViewModel: StorageViewModel = viewModel()
    val state by storageViewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var zoneToEdit by remember { mutableStateOf<StorageZone?>(null) }
    var zoneToDelete by remember { mutableStateOf<StorageZone?>(null) }

    Scaffold(
        topBar = { TopAppBar( title = { Text("Storage Zones") } ) },
        floatingActionButton = {
            FloatingActionButton( onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add New Zone")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn {
                items(items = state.zones) { zone ->
                    StorageZoneCard(
                        zone = zone,
                        onEdit = { zoneToEdit = zone },
                        onDelete = { zoneToDelete = zone }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        state.errorMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { storageViewModel.clearError() },
                confirmButton = { TextButton(onClick = { storageViewModel.clearError() }) { Text("OK") } },
                title = { Text("Action Blocked") },
                text = { Text(msg) }
            )
        }

    }
    if (showAddDialog) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        StorageInputDialog(
            title = "New Storage Zone",
            onConfirm = { name ->
                storageViewModel.addZone(userId, name)
                showAddDialog = false
                storageViewModel.refresh()
            },
            onDismiss = { showAddDialog = false }
        )
    }

    if (zoneToEdit != null) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        StorageInputDialog(
            title = "Edit Zone Name",
            initialValue = zoneToEdit!!.id,
            onConfirm = { newName ->
                storageViewModel.updateZone(userId, zoneToEdit!!.copy(zoneName = newName))
                zoneToEdit = null
            },
            onDismiss = { zoneToEdit = null }
        )
    }

    if (zoneToDelete != null) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        AlertDialog(
            onDismissRequest = { zoneToDelete = null },
            title = { Text("Delete ${zoneToDelete!!.id}?") },
            text = { Text("Are you sure? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        storageViewModel.deleteZone(userId, zoneToDelete!!)
                        zoneToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { zoneToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun StorageZoneCard(zone: StorageZone, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = zone.zoneName, style = MaterialTheme.typography.titleMedium)
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun StorageInputDialog(
    title: String,
    initialValue: String = "",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Zone Name (e.g. Fridge)") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}