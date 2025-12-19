package com.jose.pantrypal.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.jose.pantrypal.items.FakeItemRepository
import com.jose.pantrypal.items.FirestoreItemRepository
import com.jose.pantrypal.items.ItemRepository
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.Timestamp
import java.time.LocalDate


class StorageViewModel(
    private val itemRepository: ItemRepository = FirestoreItemRepository(),
    private val storageRepository: StorageRepository = FirestoreStorageRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorageUiState(isLoading = true))
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = StorageUiState(isLoading = true)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        viewModelScope.launch {
            try {
                val zones = storageRepository.getZonesForUser(uid)
                _uiState.value = _uiState.value.copy(
                    zones = zones,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load storage zone."
                )
            }
        }
    }

    fun addZone(userId: String, name: String) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Zone name cannot be blank."
            )
        }
        viewModelScope.launch {
            try {
                val currentTimestamp = System.currentTimeMillis()
                val newZone = StorageZone(name, currentTimestamp)
                storageRepository.addZone(userId, newZone)
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add storage zone."
                )
            }
        }
    }

    fun deleteZone(zone: StorageZone) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return
        viewModelScope.launch {
            try {
                val count = itemRepository.getItemsForUser(uid).size
                if (count > 0) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Cannot delete zone with items present."
                    )
                } else {
                    storageRepository.deleteZone( uid, zone.zoneName)
                    refresh()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete storage zone."
                )
            }
        }
    }

    fun updateZone(userId: String, zoneName: StorageZone) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return
        viewModelScope.launch {
            try {
                storageRepository.updateZone(uid, zoneName)
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update storage zone."
                )
            }
        }
    }
}
