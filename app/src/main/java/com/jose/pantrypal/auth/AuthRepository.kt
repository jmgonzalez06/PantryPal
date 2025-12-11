package com.jose.pantrypal.auth

interface AuthRepository {

    fun signIn(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    )

    fun signUp(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    )

    fun getCurrentUser(): AuthUser?

    fun signOut()
}