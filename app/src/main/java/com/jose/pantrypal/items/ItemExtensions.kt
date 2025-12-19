package com.jose.pantrypal.items

import java.time.LocalDate
import java.time.ZoneId

fun Item.expiryAsLocalDate(): LocalDate? =
    this.expiryDate
        ?.toDate()
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalDate()
