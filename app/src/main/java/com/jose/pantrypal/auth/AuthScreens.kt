package com.jose.pantrypal.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginRoute(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val state by authViewModel.loginUiState.collectAsState()
    val resetState by authViewModel.resetUiState.collectAsState()

    // Navigate when login succeeds
    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            authViewModel.onLoginNavigated()
            onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onEmailChange = authViewModel::onLoginEmailChange,
        onPasswordChange = authViewModel::onLoginPasswordChange,
        onLoginClick = authViewModel::login,
        onNavigateToSignUp = onNavigateToSignUp,
        resetState = resetState,
        onResetEmailChange = authViewModel::onResetEmailChange,
        onSendPasswordReset = authViewModel::sendPasswordReset,
        onDismissResetDialogMessage = authViewModel::clearResetMessage
    )
}

@Composable
fun SignUpRoute(
    onNavigateToLogin: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val state by authViewModel.signUpUiState.collectAsState()

    // Navigate back to login when sign up succeeds
    LaunchedEffect(state.isSignUpSuccessful) {
        if (state.isSignUpSuccessful) {
            authViewModel.onSignUpNavigated()
            onNavigateToLogin()
        }
    }

    SignUpScreen(
        state = state,
        onEmailChange = authViewModel::onSignUpEmailChange,
        onPasswordChange = authViewModel::onSignUpPasswordChange,
        onConfirmPasswordChange = authViewModel::onSignUpConfirmPasswordChange,
        onSignUpClick = authViewModel::signUp,
        onNavigateToLogin = onNavigateToLogin
    )
}

// UI-only composable

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    resetState: PasswordResetUiState,
    onResetEmailChange: (String) -> Unit,
    onSendPasswordReset: () -> Unit,
    onDismissResetDialogMessage: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (state.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(18.dp)
                )
            } else {
                Text("Log In")
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                if (resetState.email.isBlank() && state.email.isNotBlank()) {
                    onResetEmailChange(state.email)
                }
                showResetDialog = true
            },
            enabled = !state.isLoading
        ) {
            Text("Forgot password?")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onNavigateToSignUp,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create an account")
        }
    }
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismissResetDialogMessage()
                showResetDialog = false
            },
            title = { Text("Reset Password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = resetState.email,
                        onValueChange = onResetEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (resetState.message != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = resetState.message,
                            color = if (resetState.isError)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onSendPasswordReset() },
                    enabled = !resetState.isSending
                ) {
                    Text(if (resetState.isSending) "Sending..." else "Send")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissResetDialogMessage()
                        showResetDialog = false
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SignUpScreen(
    state: SignUpUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up")

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (state.errorMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSignUpClick,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(18.dp)
                )
            } else {
                Text("Sign Up")
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onNavigateToLogin,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Login")
        }
    }
}
