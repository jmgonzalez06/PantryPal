package com.jose.pantrypal.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jose.pantrypal.items.FirestoreItemRepository
import com.jose.pantrypal.items.Item
import com.jose.pantrypal.items.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class DashboardViewModel(
    private val itemRepository: ItemRepository = FirestoreItemRepository()
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
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw IllegalStateException("User not logged in")

                val items = itemRepository.getItemsForUser(userId)
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

        fun Item.toLocalDate(): LocalDate? =
            this.expiryDate
                ?.toDate()
                ?.toInstant()
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate()

        val expiringToday = items.count { it.toLocalDate() == today }

        val expiringSoon = items.count {
            val date = it.toLocalDate()
            date != null &&
                    (date.isAfter(today) || date.isEqual(today)) &&
                    (date.isBefore(soonThreshold) || date.isEqual(soonThreshold))
        }

        return DashboardSummary(
            expiringToday = expiringToday,
            expiringSoon = expiringSoon,
            totalItems = items.size
        )
    }
}
