package com.jose.pantrypal.storage

import java.security.Timestamp

data class StorageZone(
    val zoneName: String = "",
    val createdAt: Timestamp,
)

data class StorageUiState(
    val zones: List<StorageZone> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)