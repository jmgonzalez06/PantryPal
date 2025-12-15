package com.jose.pantrypal.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val repository: AuthRepository = FirebaseAuthRepository(FirebaseAuth.getInstance())
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState.asStateFlow()

    private val _resetUiState = MutableStateFlow(
        PasswordResetUiState()
    )
    val resetUiState: StateFlow<PasswordResetUiState> =
        _resetUiState.asStateFlow()

    val currentUser: AuthUser?
        get() = repository.getCurrentUser()

    // Login form updates

    fun onLoginEmailChange(newEmail: String) {
        _loginUiState.value = _loginUiState.value.copy(
            email = newEmail,
            errorMessage = null
        )
    }

    fun onLoginPasswordChange(newPassword: String) {
        _loginUiState.value = _loginUiState.value.copy(
            password = newPassword,
            errorMessage = null
        )
    }

    // SignUp form updates

    fun onSignUpEmailChange(newEmail: String) {
        _signUpUiState.value = _signUpUiState.value.copy(
            email = newEmail,
            errorMessage = null
        )
    }

    fun onSignUpPasswordChange(newPassword: String) {
        _signUpUiState.value = _signUpUiState.value.copy(
            password = newPassword,
            errorMessage = null
        )
    }

    fun onSignUpConfirmPasswordChange(newConfirmPassword: String) {
        _signUpUiState.value = _signUpUiState.value.copy(
            confirmPassword = newConfirmPassword,
            errorMessage = null
        )
    }

    // Actions

    fun login() {
        val current = _loginUiState.value
        val email = current.email.trim()
        val password = current.password

        if (email.isBlank() || password.isBlank()) {
            _loginUiState.value = current.copy(
                errorMessage = "Email and password are required."
            )
            return
        }

        if (password.length < 6) {
            _loginUiState.value = current.copy(
                errorMessage = "Password must be at least 6 characters."
            )
            return
        }

        _loginUiState.value = current.copy(
            isLoading = true,
            errorMessage = null
        )

        repository.signIn(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                }
                is AuthResult.Error -> {
                    _loginUiState.value = _loginUiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isLoginSuccessful = false
                    )
                }
            }
        }
    }

    fun signUp() {
        val current = _signUpUiState.value
        val email = current.email.trim()
        val password = current.password
        val confirm = current.confirmPassword

        if (email.isBlank() || password.isBlank() || confirm.isBlank()) {
            _signUpUiState.value = current.copy(
                errorMessage = "All fields are required."
            )
            return
        }

        if (password.length < 6) {
            _signUpUiState.value = current.copy(
                errorMessage = "Password must be at least 6 characters."
            )
            return
        }

        if (password != confirm) {
            _signUpUiState.value = current.copy(
                errorMessage = "Passwords do not match."
            )
            return
        }

        _signUpUiState.value = current.copy(
            isLoading = true,
            errorMessage = null
        )

        repository.signUp(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    _signUpUiState.value = _signUpUiState.value.copy(
                        isLoading = false,
                        isSignUpSuccessful = true
                    )
                }
                is AuthResult.Error -> {
                    _signUpUiState.value = _signUpUiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        isSignUpSuccessful = false
                    )
                }
            }
        }
    }

    fun onLoginNavigated() {
        _loginUiState.value = _loginUiState.value.copy(
            isLoginSuccessful = false
        )
    }

    fun onSignUpNavigated() {
        _signUpUiState.value = _signUpUiState.value.copy(
            isSignUpSuccessful = false
        )
    }

    fun signOut() {
        repository.signOut()
        _loginUiState.value = LoginUiState()
        _signUpUiState.value = SignUpUiState()
    }

    //Password Reset
    fun onResetEmailChange(newEmail: String) {
        _resetUiState.value = _resetUiState.value.copy(
            email = newEmail,
            message = null
        )
    }

    fun sendPasswordReset() {
        val email = _resetUiState.value.email.trim()

        if (email.isBlank()) {
            _resetUiState.value = _resetUiState.value.copy(
                message = "Please enter your email."
            )
            return
        }

        _resetUiState.value = _resetUiState.value.copy(
            isSending = true,
            message = null
        )

        repository.sendPasswordResetEmail(email) { result ->
            when (result) {
                is AuthResult.Success -> {
                    _resetUiState.value = _resetUiState.value.copy(
                        isSending = false,
                        message = "Password reset email sent."
                    )
                }
                is AuthResult.Error -> {
                    _resetUiState.value = _resetUiState.value.copy(
                        isSending = false,
                        message = result.message
                    )
                }
            }
        }
    }

    fun clearResetMessage() {
        _resetUiState.value = _resetUiState.value.copy(
            message = null
        )
    }
}
