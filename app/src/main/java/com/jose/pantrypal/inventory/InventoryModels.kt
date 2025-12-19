package com.jose.pantrypal.inventory

import com.google.firebase.Timestamp
import com.jose.pantrypal.items.Item
import com.jose.pantrypal.storage.StorageZone

data class InventoryUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedZoneId: String? = null,
    val filteredItems: List<Item> = emptyList(),
    val storageZones: List<StorageZone> = emptyList(),
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.EXPIRY_ASC,
)

enum class SortOption {
    EXPIRY_ASC,
    EXPIRY_DESC
}


data class AddItemUiState(
    val name: String = "",
    val expiryDate: Timestamp? = null,
    val quantity: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
