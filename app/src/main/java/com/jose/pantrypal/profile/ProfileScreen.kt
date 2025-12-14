package com.jose.pantrypal.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jose.pantrypal.auth.AuthViewModel

@Composable
fun ProfileRoute(
    onSignedOut: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val userEmail = authViewModel.currentUser?.email

    ProfileScreen(
        userEmail = userEmail,
        onSignOutClick = {
            authViewModel.signOut()
            onSignedOut()
        }
    )
}

@Composable
fun ProfileScreen(
    userEmail: String?,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile")

        Spacer(Modifier.height(16.dp))

        if (userEmail != null) {
            Text("Signed in as: $userEmail")
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = onSignOutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out")
        }
    }
}
