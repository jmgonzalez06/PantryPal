package com.jose.pantrypal.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.jose.pantrypal.items.FirestoreItemRepository
import com.jose.pantrypal.items.ItemRepository
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID



class StorageViewModel(
    private val itemRepository: ItemRepository = FirestoreItemRepository(),
    private val storageRepository: StorageRepository = FirestoreStorageRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorageUiState(isLoading = true))
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = StorageUiState(isLoading = true)
        val uid = userId ?: return

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

    fun addZone(name: String) {
        val uid = userId ?: return
        addZone(uid, name)
    }

    fun deleteZone(zone: StorageZone) {
        val uid = userId ?: return
        deleteZone(uid, zone)
    }

    fun updateZone(zone: StorageZone) {
        val uid = userId ?: return
        updateZone(uid, zone)
    }

    fun renameZone(zone: StorageZone, newName: String) {
        val uid = userId ?: return
        val trimmed = newName.trim()

        if (trimmed.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Zone name cannot be blank."
            )
            return
        }

        viewModelScope.launch {
            try {
                storageRepository.updateZone(
                    uid,
                    zone.copy(zoneName = trimmed)
                )
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to rename storage zone."
                )
            }
        }
    }


    fun addZone(userId: String, name: String) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Zone name cannot be blank.")
            return
        }
        viewModelScope.launch {
            try {
                val newZone = StorageZone(
                    id = UUID.randomUUID().toString(),
                    zoneName = name
                )

                storageRepository.addZone(userId, newZone)
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add storage zone."
                )
            }
        }
    }

    fun deleteZone(userId: String, zone: StorageZone) {

        viewModelScope.launch {
            try {
                val allItems = itemRepository.getItemsForUser(userId)
                val itemsInZone = allItems.filter { it.zoneId == zone.id }
                if (itemsInZone.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Cannot delete zone with items present."
                    )
                } else {
                    storageRepository.deleteZone(userId, zone.zoneName)
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
        viewModelScope.launch {
            try {
                storageRepository.updateZone(userId, zoneName)
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update storage zone."
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
