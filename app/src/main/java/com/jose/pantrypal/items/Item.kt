package com.jose.pantrypal.items

import com.google.firebase.Timestamp
import java.time.LocalDate

data class Item(
    val id: String = "",
    val name: String = "",
    val expiryDate: Timestamp? = null
)