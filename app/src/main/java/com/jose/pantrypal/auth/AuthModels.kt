package com.jose.pantrypal.auth

data class AuthUser(
    val uid: String,
    val email: String?
)

sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
