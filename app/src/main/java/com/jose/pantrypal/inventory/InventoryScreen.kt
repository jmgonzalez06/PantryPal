package com.jose.pantrypal.inventory

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jose.pantrypal.items.FakeItemRepository
import com.jose.pantrypal.items.Item
import com.jose.pantrypal.items.ItemRepository
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import androidx.compose.material.icons.filled.Sort

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
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    label = { Text("Search Pantry") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )

                IconButton(onClick = {
                    val nextSort = when (uiState.sortOption) {
                        SortOption.EXPIRY_ASC -> SortOption.EXPIRY_DESC
                        SortOption.EXPIRY_DESC -> SortOption.EXPIRY_ASC
                    }
                    viewModel.onSortOptionChanged(nextSort)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort"
                    )
                }
            }

            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                FilterChip(
                    selected = uiState.selectedZoneId == null,
                    onClick = { viewModel.onZoneFilterChange(null) },
                    label = { Text("All Zones")}
                )
                Spacer(Modifier.width(8.dp))
                // TODO: Add Zones Repository
                uiState.storageZones.forEach { zone ->
                    FilterChip(
                        selected = uiState.selectedZoneId == zone.zoneName,
                        onClick = { viewModel.onZoneFilterChange(zone.zoneName) },
                        label = { Text(zone.zoneName) }
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }

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
                Text(item.name, style = MaterialTheme.typography.titleMedium, color = indicatorColor)
                Text("Quantity: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                Text("Expires in $daysRemaining days", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
