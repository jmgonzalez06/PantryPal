package com.jose.pantrypal.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.jose.pantrypal.items.FakeItemRepository
import com.jose.pantrypal.items.ItemRepository
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class StorageViewModel(
    // TODO: Add real storage repository when online
    private val itemRepository: ItemRepository = FakeItemRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorageUiState(isLoading = true))
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = StorageUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val items = itemRepository.getItemsForUser(userId = "placeholder")

                _uiState.value = StorageUiState(
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = StorageUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load storage zone."
                )
            }
        }
    }

    fun addZone(name: String) {

    }
    // TODO: delete zone must check for items before deleting
    fun deleteZone(zoneId: String) {

    }

    fun renameZone(zoneId: String, newName: String) {

    }
}
