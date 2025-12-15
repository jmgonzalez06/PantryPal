package com.jose.pantrypal.items

import java.time.LocalDate

// Temporary repository used only until the FirestoreItemRepository is implemented.

class FakeItemRepository : ItemRepository {

    override suspend fun getItemsForUser(userId: String): List<Item> {
        val today = LocalDate.now()
        return listOf(
            Item(id = "1", name = "Milk", expiryDate = today),
            Item(id = "2", name = "Eggs", expiryDate = today.plusDays(2)),
            Item(id = "3", name = "Spinach", expiryDate = today.plusDays(3)),
            Item(id = "4", name = "Pasta", expiryDate = today.plusDays(10)),
            Item(id = "5", name = "Chicken", expiryDate = today.plusDays(1))
        )
    }
}
