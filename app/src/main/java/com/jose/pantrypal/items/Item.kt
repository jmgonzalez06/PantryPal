package com.jose.pantrypal.items

import java.time.LocalDate

data class Item(
    val id: String,
    val name: String,
    val expiryDate: LocalDate
)