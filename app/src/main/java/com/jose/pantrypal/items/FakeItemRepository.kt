package com.jose.pantrypal.items

import com.google.firebase.Timestamp
import java.time.LocalDate

// Temporary repository used only until the FirestoreItemRepository is implemented.

class FakeItemRepository : ItemRepository {

    override suspend fun getItemsForUser(userId: String): List<Item> {
        val today = LocalDate.now()
        return listOf(
            Item(id = "1", name = "Milk", expiryDate = Timestamp.now()),
            Item(id = "2", name = "Eggs", expiryDate = Timestamp.now()),
            Item(id = "3", name = "Spinach", expiryDate = Timestamp.now()),
            Item(id = "4", name = "Pasta", expiryDate = Timestamp.now()),
            Item(id = "5", name = "Chicken", expiryDate = Timestamp.now())
        )
    }

    override suspend fun createItem(userId: String, item: Item) {
        // no-op (fake repository)
    }

    override suspend fun updateItem(userId: String, item: Item) {
        // no-op (fake repository)
    }

    override suspend fun deleteItem(userId: String, itemId: String) {
        // no-op (fake repository)
    }
}
