package com.jose.pantrypal.auth

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun signIn(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        onResult(
                            AuthResult.Success(
                                AuthUser(
                                    uid = user.uid,
                                    email = user.email
                                )
                            )
                        )
                    } else {
                        onResult(AuthResult.Error("No user returned from Firebase"))
                    }
                } else {
                    val message = task.exception?.localizedMessage
                        ?: "Sign in failed"
                    onResult(AuthResult.Error(message))
                }
            }
    }

    override fun signUp(
        email: String,
        password: String,
        onResult: (AuthResult) -> Unit
    ) {
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        onResult(
                            AuthResult.Success(
                                AuthUser(
                                    uid = user.uid,
                                    email = user.email
                                )
                            )
                        )
                    } else {
                        onResult(AuthResult.Error("No user returned from Firebase"))
                    }
                } else {
                    val message = task.exception?.localizedMessage
                        ?: "Sign up failed"
                    onResult(AuthResult.Error(message))
                }
            }
    }

    override fun sendPasswordResetEmail(
        email: String,
        onResult: (AuthResult) -> Unit
    ) {
        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // No user object returned; use Success with a lightweight placeholder user
                    onResult(
                        AuthResult.Success(
                            AuthUser(
                                uid = "",
                                email = email
                            )
                        )
                    )
                } else {
                    val message = task.exception?.localizedMessage
                        ?: "Failed to send reset email."
                    onResult(AuthResult.Error(message))
                }
            }
    }

    override fun getCurrentUser(): AuthUser? {
        val user = firebaseAuth.currentUser
        return if (user != null) {
            AuthUser(uid = user.uid, email = user.email)
        } else {
            null
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
