package com.example.helphive1.ui.composable

import com.example.helphive1.R
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.helphive1.Screen
import com.example.helphive1.model.DBGestor
import com.example.helphive1.ui.composable.login.LoginPageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@Composable
fun loginPage(navController: NavController) {
    val context = LocalContext.current
    val viewModel = remember { LoginPageViewModel() }
    val auth: FirebaseAuth = Firebase.auth
    val gestor = DBGestor

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF7B00.toInt()),
                        Color(0xFFFF8800.toInt()),
                        Color(0xFFFF9500.toInt()),
                        Color(0xFFFFA200.toInt()),
                        Color(0xFFFFAA00.toInt()),
                        Color(0xFFFFB700.toInt()),
                        Color(0xFFFFC300.toInt()),
                        Color(0xFFFFDD00.toInt()),
                        Color(0xFFFFEA00.toInt())
                    )
                )
            )
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            RowImage()

            Spacer(modifier = Modifier.height(40.dp))


            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = 20.dp,
                backgroundColor = Color.White 

            ) {
                Column(Modifier.padding(16.dp)) {
                    //    RowImage()
                    RowEmail(
                        email = viewModel.email,
                        emailChange = viewModel::onEmailChange,
                        isValid = viewModel.isValidEmail
                    )

                    RowPassword(
                        contrasena = viewModel.contrasena,
                        passwordChange = viewModel::onPasswordChange,
                        passwordVisible = viewModel.passwordVisible,
                        passwordVisibleChange = viewModel::togglePasswordVisibility,
                        isValidPassword = viewModel.isValidPassword
                    )

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                val dbGestor = DBGestor.getInstance()
                                dbGestor.signInWithEmailPassword(
                                    email = viewModel.email,
                                    password = viewModel.contrasena,
                                    onSuccess = {
                                        // Navegar a la pantalla de inicio después de iniciar sesión correctamente
                                        navController.navigate(Screen.HOME.ruta) {
                                            popUpTo(Screen.LOGIN.ruta)
                                        }
                                    },
                                    onFailure = { error ->
                                        Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(android.graphics.Color.parseColor("#aaeeaa"))),
                            enabled = viewModel.isValidEmail && viewModel.isValidPassword
                        ) {
                            Text(text = "Iniciar Sesión")
                        }


                    }



                    Row(
                        Modifier
                            .fillMaxWidth()
                            ,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                navController.navigate(Screen.REGISTER.ruta) {
                                    popUpTo(Screen.LOGIN.ruta)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(android.graphics.Color.parseColor("#aaeeaa")))
                            ,

                        ) {
                            Text(text = " Registrarme ")
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun RowPassword(
    contrasena: String,
    passwordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordVisibleChange: () -> Unit,
    isValidPassword: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = contrasena,
            onValueChange = passwordChange,
            maxLines = 1,
            singleLine = true,
            label = { Text(text = "Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Filled.VisibilityOff
                } else {
                    Icons.Filled.Visibility
                }
                IconButton(
                    onClick = passwordVisibleChange
                ) {
                    Icon(
                        imageVector = image,
                        contentDescription = "Ver contraseña"
                    )
                }
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            colors = if (isValidPassword) {
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Green,
                    focusedBorderColor = Color.Green
                )
            } else {
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Red,
                    focusedBorderColor = Color.Red
                )
            }

        )
    }
}


@Composable
fun RowEmail(
    email: String,
    emailChange: (String) -> Unit,
    isValid: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = emailChange,
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            maxLines = 1,
            singleLine = true,
            colors = if (isValid) {
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Green,
                    focusedBorderColor = Color.Green
                )
            } else {
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = androidx.compose.ui.graphics.Color.Red,
                    focusedBorderColor = androidx.compose.ui.graphics.Color.Red
                )
            }
        )
    }
}



    @Composable
    fun RowImage() {
        Row(
            Modifier
                .fillMaxWidth()
            //   .padding(10.dp)
            ,
            horizontalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(350.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Imagen login",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

        }
    }
