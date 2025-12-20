package com.jose.pantrypal.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme

@Composable
fun DashboardScreen(
    onExpiringTodayClick: () -> Unit,
    onExpiringSoonClick: () -> Unit,
    onTotalItemsClick: () -> Unit
) {
    val dashboardViewModel: DashboardViewModel = viewModel()
    val state by dashboardViewModel.uiState.collectAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                dashboardViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                Text(
                    text = state.errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = dashboardViewModel::refresh) {
                    Text("Retry")
                }
            }

            state.summary != null -> {
                val summary = state.summary!!
                DashboardSummaryCard(
                    title = "Expiring Today",
                    value = summary.expiringToday,
                    onClick = onExpiringTodayClick
                )

                Spacer(Modifier.height(12.dp))

                DashboardSummaryCard(
                    title = "Expiring Soon (3 days)",
                    value = summary.expiringSoon,
                    onClick = onExpiringSoonClick
                )

                Spacer(Modifier.height(12.dp))

                DashboardSummaryCard(
                    title = "Total Items",
                    value = summary.totalItems,
                    onClick = onTotalItemsClick
                )
            }
        }
    }
}

@Composable
private fun DashboardSummaryCard(
    title: String,
    value: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tap to view",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
