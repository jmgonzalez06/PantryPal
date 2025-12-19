package com.jose.pantrypal.items

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreItemRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ItemRepository {

    override suspend fun getItemsForUser(userId: String): List<Item> {
        val snapshot = firestore
            .collection("users")
            .document(userId)
            .collection("items")
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(Item::class.java)?.copy(id = document.id)
        }
    }

    override suspend fun createItem(userId: String, item: Item) {
        firestore
            .collection("users")
            .document(userId)
            .collection("items")
            .add(item)
            .await()
    }

    override suspend fun updateItem(userId: String, item: Item) {
        firestore
            .collection("users")
            .document(userId)
            .collection("items")
            .document(item.id)
            .set(item)
            .await()
    }

    override suspend fun deleteItem(userId: String, itemId: String) {
        firestore
            .collection("users")
            .document(userId)
            .collection("items")
            .document(itemId)
            .delete()
            .await()
    }
}