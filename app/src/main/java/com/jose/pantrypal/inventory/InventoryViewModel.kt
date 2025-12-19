package com.jose.pantrypal.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jose.pantrypal.items.FirestoreItemRepository
import com.jose.pantrypal.items.ItemRepository
import com.jose.pantrypal.storage.StorageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth



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

    // TODO: Add search function (probably similar to UserDirectory project) and filters (sorting or by zone?)
}