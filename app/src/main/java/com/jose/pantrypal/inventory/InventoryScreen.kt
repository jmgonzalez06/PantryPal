package com.jose.pantrypal.inventory

import androidx.compose.foundation.background
import com.jose.pantrypal.items.expiryAsLocalDate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jose.pantrypal.items.Item
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onItemClick: (String) -> Unit,
    onAddItemClick: () -> Unit,
    viewModel: InventoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Item") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Item") },
                onClick = onAddItemClick
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { newValue ->
                        viewModel.updateSearchQuery(newValue)
                    },
                    label = { Text("Search Pantry") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )

                IconButton(onClick = {
                    val nextSort = when (uiState.sortOption) {
                        SortOption.EXPIRY_ASC -> SortOption.EXPIRY_DESC
                        SortOption.EXPIRY_DESC -> SortOption.EXPIRY_ASC
                    }
                    viewModel.onSortOptionChanged(nextSort)
                }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
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
    val expiryLocalDate = item.expiryAsLocalDate()

    val daysRemaining = expiryLocalDate?.let {
        ChronoUnit.DAYS.between(LocalDate.now(), it)
    } ?: 0

    val indicatorColor = when {
        daysRemaining <= 0 -> Color(0xFFD32F2F)
        daysRemaining < 3 -> Color(0xFFFBC02D)
        else -> Color(0xFF388E3C)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("Quantity: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                Text("Expires in $daysRemaining days", style = MaterialTheme.typography.bodySmall)
            }
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(indicatorColor, shape = RoundedCornerShape(4.dp))
            )
        }
    }
}
