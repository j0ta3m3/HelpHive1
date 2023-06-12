package com.example.helphive1.reusable

import PublicacionViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.helphive1.Screen
import com.example.helphive1.model.Publication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@Composable
fun NavigationButtons(navController: NavController,usuarioId: String , onPublicacionAdded: (Publication) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = { navController.navigate(Screen.HOME.ruta) },
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF38B000.toInt()), shape = CircleShape)
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
        }


        IconButton(
            onClick = { navController.navigate(Screen.SEARCH.ruta) },
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF38B000.toInt()), shape = CircleShape)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
        }

        IconButton(
            onClick = { navController.navigate(Screen.PROFILE.ruta) },
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color(0xFF38B000.toInt()), shape = CircleShape
                )
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
        }

        AddPublicacionButton(onPublicacionAdded, usuarioId)
    }
}

@Composable
fun AddPublicacionButton(
    onPublicacionAdded: (Publication) -> Unit,
    usuarioId: String // ID del usuario actual
) {
    val showDialog = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val remuneration = remember { mutableStateOf("") }
    val localizacion = remember { mutableStateOf("") }
    val estado = remember { mutableStateOf("Disponible") }
    val idWorkwer = remember { mutableStateOf("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Añadir nueva publicación") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title.value,
                        onValueChange = { title.value = it },
                        label = { Text("Título") }
                    )
                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = { Text("Descripción") }
                    )
                    OutlinedTextField(
                        value = category.value,
                        onValueChange = { category.value = it },
                        label = { Text("Categoría") }
                    )
                    OutlinedTextField(
                        value = remuneration.value,
                        onValueChange = { remuneration.value = it.filterRemuneration() },
                        label = { Text("Remuneración") }
                    )
                    OutlinedTextField(
                        value = localizacion.value,
                        onValueChange = { localizacion.value = it },
                        label = { Text("Localización") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nuevaPublicacion = Publication(
                            id = UUID.randomUUID().toString(),
                            titulo = title.value,
                            descripcion = description.value,
                            categoria = category.value,
                            remuneracion = remuneration.value.toDouble(),
                            autorId = usuarioId,
                            localizacion = localizacion.value,
                            estado = estado.value,
                            idWorker = idWorkwer.value
                        )
                        onPublicacionAdded(nuevaPublicacion)
                        showDialog.value = false
                    }
                ) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    FloatingActionButton(
        onClick = { showDialog.value = true },
        backgroundColor = Color(0xFF38B000.toInt())
    ) {
        Icon(Icons.Default.Add, contentDescription = "Añadir Publicación", tint = Color.White)
    }
}

private fun String.filterRemuneration(): String {
    return this.replace(Regex("[^0-9,.]"), "")
}


@Composable
fun Indicadores(
    trabajosRealizados: Int,
    trabajosOfrecidos: Int
) {
    val cornerSize = 6.dp // Tamaño de curvatura de las esquinas


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .background(
                    Color.White, shape = CutCornerShape(
                        topStart = cornerSize,
                        topEnd = cornerSize,
                        bottomEnd = cornerSize,
                        bottomStart = cornerSize
                    )
                )
        ) {
            Text(
                text = "Realizados",
                style = MaterialTheme.typography.h5,
                color = Color(0xFF38B000.toInt()),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = trabajosRealizados.toString(),
                style = MaterialTheme.typography.h6,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .background(
                    Color.White, shape = CutCornerShape(
                        topStart = cornerSize,
                        topEnd = cornerSize,
                        bottomEnd = cornerSize,
                        bottomStart = cornerSize
                    )
                )
        ) {
            Text(
                text = "Ofrecidos",
                style = MaterialTheme.typography.h5,
                color = Color.Blue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = trabajosOfrecidos.toString(),
                style = MaterialTheme.typography.h6,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
