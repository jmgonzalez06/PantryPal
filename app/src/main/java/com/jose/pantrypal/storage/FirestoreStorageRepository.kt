package com.jose.pantrypal.storage

import com.google.firebase.firestore.FirebaseFirestore
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
            val id = document.getString("id")
            val zoneName = document.getString("zoneName")

            if (id != null && zoneName != null) {
                StorageZone(
                    id = id,
                    zoneName = zoneName
                )
            } else {
                null
            }
        }
    }

    override suspend fun addZone(userId: String, zone: StorageZone) {
        firestore
            .collection("users")
            .document(userId)
            .collection("storageZones")
            .document(zone.id)
            .set(
                mapOf(
                    "id" to zone.id,
                    "zoneName" to zone.zoneName
                )
            )
            .await()
    }

    override suspend fun updateZone(userId: String, zone: StorageZone) {
        firestore
            .collection("users")
            .document(userId)
            .collection("storageZones")
            .document(zone.id)
            .set(
                mapOf(
                    "id" to zone.id,
                    "zoneName" to zone.zoneName
                )
            )
            .await()
    }

    override suspend fun deleteZone(userId: String, zoneId: String) {
        firestore
            .collection("users")
            .document(userId)
            .collection("storageZones")
            .document(zoneId)
            .delete()
            .await()
    }
}
