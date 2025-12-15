package com.jose.pantrypal.dashboard

import java.time.LocalDate

data class PantryItem(
    val name: String,
    val expiryDate: LocalDate
)

data class DashboardSummary(
    val expiringToday: Int,
    val expiringSoon: Int,
    val totalItems: Int
)

data class DashboardUiState(
    val isLoading: Boolean = false,
    val summary: DashboardSummary? = null,
    val errorMessage: String? = null
)
