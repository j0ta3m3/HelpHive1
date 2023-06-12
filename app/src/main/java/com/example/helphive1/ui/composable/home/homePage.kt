package com.example.helphive1.ui.composable.home
import PublicacionViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.helphive1.reusable.NavigationButtons
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import com.example.helphive1.Screen
import com.example.helphive1.model.Publication
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun HomeScreen(navController: NavController, publications: List<Publication>?) {
    val context = LocalContext.current
    val publicacionViewModel = remember { PublicacionViewModel() }

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val usuarioId = currentUser?.uid ?: ""

    val onPublicationAdded: (Publication) -> Unit = { publication ->
        publicacionViewModel.addPublicacion(publication,
            onSuccess = {
                Toast.makeText(context, "Publicación añadida exitosamente", Toast.LENGTH_SHORT).show()
            },
            onFailure = { errorMessage ->
                Toast.makeText(context, "Error al añadir la publicación: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    val onRefreshClicked: () -> Unit = {
        publicacionViewModel.loadPublicaciones(
            onSuccess = {
                Toast.makeText(context, "Publicaciones actualizadas", Toast.LENGTH_SHORT).show()
            },
            onFailure = { errorMessage ->
                Toast.makeText(context, "Error al actualizar las publicaciones: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tablón de anuncios") },
                backgroundColor = Color(android.graphics.Color.parseColor("#aaeeaa")),
                contentColor = Color.White
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
        ) {
            if (publications != null) {

                Log.d("","hay ${publications.count()} posts")


                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(publications) { publication ->

                        Log.d("","ESTADO: ${publication.estado} ")
                        Log.d("","TITULO: ${publication.titulo} ")
                        Log.d("","ID: ${publication.id} ")

                        PublicationItem(publication = publication, navController = navController)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Button(
                onClick = onRefreshClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Actualizar publicaciones")
            }

            NavigationButtons(navController = navController, usuarioId, onPublicationAdded)
        }
    }
}


@Composable
fun PublicationItem(publication: Publication, navController: NavController) {
    if (publication.estado.equals("Disponible", ignoreCase = true)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.PUBLICATION_DETAILS.ruta + "/${publication.id}") }
                .padding(16.dp),
            backgroundColor = Color.White,
            elevation = 8.dp,
            contentColor = Color.DarkGray
        ) {
            Log.d("publi","${publication.titulo}")
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = publication.titulo,
                    style = MaterialTheme.typography.h5
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = publication.descripcion,
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Remuneración: ${publication.remuneracion} €",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Categoría: ${publication.categoria}",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Lugar : ${publication.localizacion.uppercase()}",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}


/*

@Preview
@Composable
fun HomeScreenPreview() {
   HomeScreen()
}

 */