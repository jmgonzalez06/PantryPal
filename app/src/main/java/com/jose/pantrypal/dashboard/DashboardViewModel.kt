package com.jose.pantrypal.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jose.pantrypal.items.FakeItemRepository
import com.jose.pantrypal.items.Item
import com.jose.pantrypal.items.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(
    private val itemRepository: ItemRepository = FakeItemRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = DashboardUiState(isLoading = true)

        viewModelScope.launch {
            try {
                // TODO: pass the real userId when FirestoreItemRepository is implemented
                val items = itemRepository.getItemsForUser(userId = "placeholder")
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
    }

    private fun computeSummary(items: List<Item>): DashboardSummary {
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
}
