package com.example.helphive1.ui.composable.publicationDetail

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.helphive1.Screen
import com.example.helphive1.model.Publication
import com.example.helphive1.model.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun PublicacionDetalle(publicacionId: String, nav: NavController) {
    val viewModel: PublicacionDetalleViewModel = viewModel()
    val publicacion: Publication? by viewModel.publicacion.observeAsState()
    val autorNombre: String? by viewModel.autorNombre.observeAsState()
    val autorTelefono: String? by viewModel.autorTelefono.observeAsState()

    LaunchedEffect(publicacionId) {
        viewModel.getPublicacion(publicacionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la publicación") },
                backgroundColor = Color(android.graphics.Color.parseColor("#aaeeaa")),
                contentColor = Color.White
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFB700.toInt()),
                            Color(0xFFFFC300.toInt()),
                            Color(0xFFFFDD00.toInt()),
                            Color(0xFFFFEA00.toInt())
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White,
                elevation = 8.dp,
                contentColor = Color.DarkGray
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    publicacion?.let { publicacion ->
                        Text(
                            text = publicacion.titulo,
                            style = MaterialTheme.typography.h5
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = publicacion.descripcion,
                            style = MaterialTheme.typography.body1
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    autorNombre?.let { nombre ->
                        Text(
                            text = "Autor: $nombre",
                            style = MaterialTheme.typography.h6
                        )
                    }
                    autorTelefono?.let { telefono ->
                        Text(
                            text = "Teléfono: $telefono",
                            style = MaterialTheme.typography.body1
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    publicacion?.let { publicacion ->
                        Button(onClick = {

                            nav.navigate(Screen.PROFILE_AUTOR.ruta + "/${publicacion.autorId}")
                        }) {
                            Text(text = "Ver Perfil")
                        }
                    }
                }
            }
        }
    }
}