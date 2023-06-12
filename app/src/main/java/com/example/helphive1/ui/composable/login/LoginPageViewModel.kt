package com.example.helphive1.ui.composable.login

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginPageViewModel : ViewModel() {
    var email by mutableStateOf("")
    var isValidEmail by mutableStateOf(false)

    var contrasena by mutableStateOf("")
    var isValidPassword by mutableStateOf(false)

    var passwordVisible by mutableStateOf(false)

    fun onEmailChange(newValue: String) {
        email = newValue
        isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun onPasswordChange(newValue: String) {
        contrasena = newValue
        isValidPassword = contrasena.length >= 6
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun login(context: Context) {
        Toast.makeText(context, "FAKE LOGIN :)", Toast.LENGTH_LONG).show()
    }
}
