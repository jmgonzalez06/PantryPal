package com.jose.pantrypal.items

interface ItemRepository {
    suspend fun getItemsForUser(userId: String): List<Item>
}