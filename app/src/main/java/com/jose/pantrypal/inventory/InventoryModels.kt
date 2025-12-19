package com.jose.pantrypal.inventory

import com.google.firebase.Timestamp
import com.jose.pantrypal.items.Item
import com.jose.pantrypal.storage.StorageZone

data class InventoryUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filteredItems: List<Item> = emptyList(),
    val storageZones: List<StorageZone> = emptyList(),
    // TODO: PDF says search, filters, sorting, and visual cue for expiry
    val searchQuery: String = "",
    // Sidenote: add storagezoneid to item for filtering?
)

data class AddItemUiState(
    val name: String = "",
    val expiryDate: Timestamp? = null,
    val quantity: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
