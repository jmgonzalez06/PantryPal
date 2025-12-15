package com.jose.pantrypal.auth

data class PasswordResetUiState(
    val email: String = "",
    val isSending: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)
