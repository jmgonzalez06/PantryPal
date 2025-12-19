package com.jose.pantrypal.storage

interface StorageRepository {
    suspend fun getZonesForUser(userId: String): List<StorageZone>

    // CRUD Operations
    suspend fun addZone(userId: String, zone: StorageZone)
    suspend fun updateZone(userId: String, zone: StorageZone)
    suspend fun deleteZone(userId: String, zoneName: String)
}