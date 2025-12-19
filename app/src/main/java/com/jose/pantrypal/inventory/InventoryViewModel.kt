package com.jose.pantrypal.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.jose.pantrypal.items.FirestoreItemRepository
import com.jose.pantrypal.items.ItemRepository
import com.jose.pantrypal.storage.StorageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.jose.pantrypal.items.Item
import java.time.LocalDate
import java.time.ZoneId


class InventoryViewModel(
    private val itemRepository: ItemRepository = FirestoreItemRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(InventoryUiState(isLoading = true))
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        _uiState.value = InventoryUiState(isLoading = true)
        // TODO: load items and storage zones
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw IllegalStateException("User not logged in")

                val items = itemRepository.getItemsForUser(userId)

                _uiState.value = _uiState.value.copy(
                    items = items,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load items to inventory."
                )
            }
        }
    }

    fun addItem(
        name: String,
        expiryDate: Timestamp?,
        quantity: Int,
        zoneId: String
    ) {
        val today = LocalDate.now()
        val expiryLocalDate = expiryDate
            ?.toDate()
            ?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDate()

        if (expiryLocalDate == null || expiryLocalDate.isBefore(today)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Expiry date must be today or later"
            )
            return
        }

        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Item name cannot be empty"
            )
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        viewModelScope.launch {
            try {
                itemRepository.createItem(
                    userId = uid,
                    item = Item(
                        name = name,
                        expiryDate = expiryDate,
                        quantity = quantity,
                        zoneId = zoneId
                    )
                )
                refresh() // reload inventory
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add item"
                )
            }
        }
    }

    fun updateItem(item: Item) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        viewModelScope.launch {
            try {
                itemRepository.updateItem(
                    userId = uid,
                    item = item
                )
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update item"
                )
            }
        }
    }

    fun deleteItem(itemId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        viewModelScope.launch {
            try {
                itemRepository.deleteItem(
                    userId = uid,
                    itemId = itemId
                )
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete item"
                )
            }
        }
    }

    fun getItemById(itemId: String): Item? {
        return uiState.value.items.firstOrNull { it.id == itemId }
    }



    // TODO: Add search function (probably similar to UserDirectory project) and filters (sorting or by zone?)
}