package com.example.helphive1.ui.composable.register

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterPageViewModel : ViewModel() {

    var nombre by mutableStateOf("")
    var isValidNombre by mutableStateOf(false)

    var apellido1 by mutableStateOf("")
    var isValidApe1 by mutableStateOf(false)

    var apellido2 by mutableStateOf("")
    var isValidApe2 by mutableStateOf(false)

    var username by mutableStateOf("")
    var isValidUsername by mutableStateOf(false)

    var email by mutableStateOf("")
    var isValidEmail by mutableStateOf(false)

    var descripcion by mutableStateOf("")
    var isValidDescripcion by mutableStateOf(false)

    var telefono by mutableStateOf("")
    var isValidTelefono by mutableStateOf(false)

    var localidad by mutableStateOf("")
    var isValidLocalidad by mutableStateOf(false)

    var password by mutableStateOf("")
    var isValidPassword by mutableStateOf(false)

    var imagen by mutableStateOf("")
    var isValidImage by mutableStateOf(false)

    var ofrecido by mutableStateOf(0)
    var isValidOfrecido by mutableStateOf(false)

    var realizado by mutableStateOf(0)
    var isValidRealizado by mutableStateOf(false)

    fun onUsernameChange(newValue: String) {
        username = newValue
        isValidUsername = username.isNotBlank()
    }

    fun onEmailChange(newValue: String) {
        email = newValue
        isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        isValidPassword = password.length >= 6
    }

    fun register(context: Context) {
        Toast.makeText(context, "FAKE REGISTER :)", Toast.LENGTH_LONG).show()
    }
}

