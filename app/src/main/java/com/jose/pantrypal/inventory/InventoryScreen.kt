package com.jose.pantrypal.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jose.pantrypal.items.FakeItemRepository
import com.jose.pantrypal.items.Item
import com.jose.pantrypal.items.ItemRepository
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onItemClick: (String) -> Unit,
    viewModel : InventoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddItemDialog by remember { mutableStateOf(false) }

    if (showAddItemDialog) {
        AddItemScreen(
            viewModel = viewModel,
            onItemAdded = {
                showAddItemDialog = false
            }
        )
        return
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Item") },
                icon = { Icon(Icons.Default.Add, "Add Item") },
                onClick = { showAddItemDialog = true }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { },
                label = { ("Search Pantry") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )

            // TODO: Make Filters for Search Bar

            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.items.size) { index ->
                    InventoryItemCard(
                        item = uiState.items[index],
                        onClick = {
                            onItemClick(uiState.items[index].id)
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: Item,
                      onClick: () -> Unit) {
    val expiryLocalDate = item.expiryDate
        ?.toDate()
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalDate()

    val daysRemaining = expiryLocalDate?.let {
        ChronoUnit.DAYS.between(LocalDate.now(), it)
    } ?: 0

    val indicatorColor = when {
        daysRemaining < 0 -> Color(0xFFD32F2F)
        daysRemaining < 3 -> Color(0xFFFBC02D)
        else -> Color.Green
    }
    /* I have the colors here, but I don't know what kinda of indicator
       Maybe text colors? Card background color is really jarring though.
     */

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                // TODO: Add Quantity Later
                Text("Expires in $daysRemaining days", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
