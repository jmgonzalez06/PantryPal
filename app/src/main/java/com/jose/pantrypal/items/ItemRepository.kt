package com.jose.pantrypal.items

interface ItemRepository {
    suspend fun getItemsForUser(userId: String): List<Item>

    suspend fun createItem(userId: String, item: Item)

    suspend fun updateItem(userId: String, item: Item)

    suspend fun deleteItem(userId: String, itemId: String)
}