package com.jose.pantrypal.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = DashboardUiState(isLoading = true)

        try {
            val items = fakeItems()

            val summary = computeSummary(items)

            _uiState.value = DashboardUiState(
                isLoading = false,
                summary = summary,
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = DashboardUiState(
                isLoading = false,
                summary = null,
                errorMessage = e.message ?: "Failed to load dashboard."
            )
        }
    }

    private fun computeSummary(items: List<PantryItem>): DashboardSummary {
        val today = LocalDate.now()
        val soonThreshold = today.plusDays(3)

        val expiringToday = items.count { it.expiryDate.isEqual(today) }

        val expiringSoon = items.count { item ->
            (item.expiryDate.isAfter(today) || item.expiryDate.isEqual(today)) &&
                    (item.expiryDate.isBefore(soonThreshold) || item.expiryDate.isEqual(soonThreshold))
        }

        return DashboardSummary(
            expiringToday = expiringToday,
            expiringSoon = expiringSoon,
            totalItems = items.size
        )
    }

    private fun fakeItems(): List<PantryItem> {
        val today = LocalDate.now()
        return listOf(
            PantryItem(name = "Milk", expiryDate = today),
            PantryItem(name = "Eggs", expiryDate = today.plusDays(2)),
            PantryItem(name = "Spinach", expiryDate = today.plusDays(3)),
            PantryItem(name = "Pasta", expiryDate = today.plusDays(10)),
            PantryItem(name = "Chicken", expiryDate = today.plusDays(1))
        )
    }
}
