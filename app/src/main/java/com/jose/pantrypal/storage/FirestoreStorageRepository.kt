package com.jose.pantrypal.storage

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FirestoreStorageRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : StorageRepository {

    override suspend fun getZonesForUser(userId: String): List<StorageZone> {
        val snapshot = firestore
            .collection("users")
            .document(userId)
            .collection("storageZones")
            .get()
            .await()
        return snapshot.documents.mapNotNull { document ->
            document.toObject(StorageZone::class.java)?.copy(zoneName = document.id)
        }
    }

    override suspend fun addZone(userId: String, zone: StorageZone) {
        firestore
            .collection("users")
            .document(userId)
            .collection("storageZones")
            .document(zone.zoneName)
            .set(mapOf(
                "id" to zone.zoneName
            ))
            .await()
    }

    override suspend fun updateZone(userId: String, zone: StorageZone) {
        firestore
            .collection("users")
            .document(userId)
            .collection("storageZones")
            .document(zone.zoneName)
            .set(mapOf(
                "id" to zone.zoneName
            ))
            .await()
    }

    override suspend fun deleteZone(userId: String, zoneName: String) {
        firestore
            .collection("users")
            .document(userId)
            .collection("storageZones")
            .document(zoneName)
            .delete()
            .await()
    }
}