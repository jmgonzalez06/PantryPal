package com.jose.pantrypal.dashboard

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
