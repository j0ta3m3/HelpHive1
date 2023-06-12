package com.example.helphive1.ui.composable.profile

import androidx.compose.material.icons.*
import PublicacionViewModel
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.helphive1.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import coil.compose.rememberImagePainter
import com.example.helphive1.Screen
import com.example.helphive1.model.DBGestor
import com.example.helphive1.model.Publication
import com.example.helphive1.reusable.NavigationButtons
import com.example.helphive1.ui.theme.HelpHive1Theme
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.tasks.await
import java.util.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.helphive1.reusable.Indicadores
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.UserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume

const val REQUEST_CODE_SELECT_PICTURE = 100

@SuppressLint("RememberReturnType", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController, profileID: String = "") {

    val firestore = Firebase.firestore
    val usuariosCollection = firestore.collection("usuarios")
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val usuarioId = currentUser?.uid

    val usuario: Boolean

    // Variable para determinar si es el perfil de usuario con la sesión iniciada o es el perfil de otro usuario
    val idToUse = if (profileID.isNotEmpty()) profileID else usuarioId

    usuario = idToUse == usuarioId

    val context = LocalContext.current

    val publicacionViewModel = remember { PublicacionViewModel() }

    val newProfilePictureUri = remember { mutableStateOf<Uri?>(null) }

    val profileVM = remember {
        ProfileViewModel()
    }

    LaunchedEffect(Unit) {
        val userId = idToUse// Obtén el ID del usuario actual
        if (userId != null) {
            profileVM.getUserOfrecidos(userId)
            profileVM.getUserRealizados(userId)
        }
    }

    val userOfrecidos by profileVM.userOfrecidos.observeAsState()

    val userRealizados by profileVM.userRealizados.observeAsState()


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            // Aquí puedes realizar acciones con la imagen seleccionada
            if (uri != null) {
                // Realiza alguna acción con la URI de la imagen
                // Por ejemplo, muestra la imagen utilizando un ImagePainter
                newProfilePictureUri.value = uri
            }
        }
    )

    LaunchedEffect(idToUse) {
        if (idToUse != null) {
            profileVM.getUserDataById(idToUse)
        }
    }


    // Función para cerrar sesión
    val cerrarSesion = {
        firebaseAuth.signOut()
        navController.navigate(Screen.LOGIN.ruta) {
            popUpTo(Screen.PROFILE.ruta) { inclusive = true }
        }
    }


    val onPublicationAdded: (Publication) -> Unit = { publication ->
        // Lógica para agregar la publicación a tu ViewModel o cualquier otra acción necesaria
        publicacionViewModel.addPublicacion(
            publication,
            onSuccess = {
                // Mostrar un mensaje de éxito utilizando un Toast
                Toast.makeText(context, "Publicación añadida exitosamente", Toast.LENGTH_SHORT)
                    .show()
            },
            onFailure = { errorMessage ->
                // Mostrar un mensaje de error utilizando un Toast
                Toast.makeText(
                    context,
                    "Error al añadir la publicación: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent,
        contentColor = Color.DarkGray
    ) {
        Box(
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(7.dp)

            ) {
                ProfileHeader(
                    onProfilePictureClicked = {
                        // Lanzar el ActivityResultLauncher
                        launcher.launch("image/*")
                    },

                    viewModel = profileVM
                )


                if (usuarioId != null) {
                    NavigationButtons(
                        navController = navController,
                        onPublicacionAdded = onPublicationAdded,
                        usuarioId = usuarioId
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                userOfrecidos?.let { of ->
                    userRealizados?.let { re ->
                        Indicadores(re, of)
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                if (idToUse != null) {
                    ProfileInfo(profileVM)
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (usuario) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = { cerrarSesion() }
                    ) {
                        Text(text = "Cerrar Sesión", color = Color.DarkGray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = if (usuario) "Mis anuncios" else "Anuncios",
                    style = MaterialTheme.typography.h6
                )

                if (idToUse != null) {
                    JobList(navController = navController, userId = idToUse)

                }

            }
        }
    }
}

@Composable
fun JobList(navController: NavController, userId: String) {
    val profileViewModel: ProfileViewModel = viewModel()
    val userPublications = profileViewModel.userPublications.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        profileViewModel.loadUserPublications(userId)
    }

    LazyColumn {
        items(userPublications.value) { publication ->
            JobListItem(job = publication) {
                // Manejar la publicación seleccionada
                // Aquí puedes hacer algo como navegar a la pantalla de detalles de la publicación
                // o mostrar información adicional sobre la publicación.
                // navController.navigate("ruta_pantalla_detalles/${publication.id}")
            }
        }
    }
}
@Composable
fun JobListItem(job: Publication, onJobClicked: () -> Unit) {
    val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid
    val showDelegarPopup = remember { mutableStateOf(false) }
    val showFinalizarPopup = remember { mutableStateOf(false) }
    val viewm: PublicacionViewModel = viewModel()
    var finalizacionExitosa by remember { mutableStateOf(false) } // Agrega el estado para la finalización exitosa

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        backgroundColor = Color(0xFF38B000.toInt()),
        elevation = 4.dp,
        contentColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = job.titulo, style = MaterialTheme.typography.h6)
            Text(text = job.descripcion, style = MaterialTheme.typography.body1)
            Text(text = "${job.remuneracion} €", style = MaterialTheme.typography.body2)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val estadoText = when (job.estado) {
                    "Disponible" -> "Delegar tarea"
                    "Inprogress" -> "Finalizar"
                    else -> ""
                }
                val estadoColor = when (job.estado) {
                    "Disponible" -> Color.White
                    "Inprogress" -> Color(0xFFFFCC00.toInt()) // Color naranja amarillento
                    "Realizado" -> Color.Red
                    else -> Color.Transparent
                }

                if (job.autorId == usuarioActual && job.estado != "Realizado") {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .padding(4.dp)
                            .clickable(onClick = {
                                if (job.estado == "Disponible") {
                                    showDelegarPopup.value = true
                                } else if (job.estado == "Inprogress") {
                                    showFinalizarPopup.value = true
                                }
                            })
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Icono de verificación",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = estadoText.uppercase(),
                                style = MaterialTheme.typography.body2,
                                color = Color.Black
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(80.dp))
                if (job.estado != "") {
                    val estadoTexto = when (job.estado) {
                        "Inprogress" -> "EN PROCESO"
                        else -> job.estado.uppercase()
                    }
                    Text(
                        text = estadoTexto,
                        style = MaterialTheme.typography.body2,
                        color = estadoColor
                    )
                }
            }
        }
    }

    if (showDelegarPopup.value) {
        DelegarPopup(
            onDismiss = { showDelegarPopup.value = false },
            onDelegarClick = { textoDelegacion ->
                viewm.delegarTarea(job.id, textoDelegacion) },
            viewModel = viewm
        )
    }

    if (showFinalizarPopup.value) {
        FinalizarPopup(
            publicacionId = job.id,
            onDismiss = { showFinalizarPopup.value = false },
            onFinalizarTareaClick = { publicacionId ->
                // Lógica para finalizar la tarea
                viewm.finalizarTarea(publicacionId)
                finalizacionExitosa = true // Actualiza el estado de finalización exitosa
            },
            finalizacionExitosa = finalizacionExitosa // Pasa el estado de finalización exitosa al Composable
        )
    }
}
@Composable
fun DelegarPopup(onDismiss: () -> Unit, onDelegarClick: (String) -> Unit,
                 viewModel: PublicacionViewModel) {
    var textoDelegar by remember { mutableStateOf("") }
    var delegarPopupState by remember { mutableStateOf(DelegarPopupState.None) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delegar Tarea") },
        text = {
            Column {
                Text(text = "Ingresa el nombre de usuario de quien realizará la tarea :")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = textoDelegar,
                    onValueChange = { textoDelegar = it },
                    label = { Text("Username : ") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    val usuarioExiste = viewModel.verificarUsuarioEnBaseDeDatos(textoDelegar)
                    if (usuarioExiste) {
                        onDelegarClick(textoDelegar)
                        delegarPopupState = DelegarPopupState.Success
                    } else {
                        delegarPopupState = DelegarPopupState.Error
                    }
                } catch (e: Exception) {
                    delegarPopupState = DelegarPopupState.Error
                }
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )

    when (delegarPopupState) {
        DelegarPopupState.Success -> {
            Toast.makeText(
                context,
                "Tarea delegada al usuario introducido" ,
                Toast.LENGTH_SHORT
            ).show()
        }
        DelegarPopupState.Error -> {
            Toast.makeText(
                context,
                "Delegación fallida, el usuario no existe",
                Toast.LENGTH_SHORT
            ).show()
        }
        else -> { /* No se muestra ningún mensaje */ }
    }
}


@Composable
fun FinalizarPopup(
    publicacionId: String,
    onDismiss: () -> Unit,
    onFinalizarTareaClick: (String) -> Unit,
    finalizacionExitosa: Boolean // Agrega un parámetro para indicar si la finalización fue exitosa
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Finalizar Tarea") },
        text = {
            Column {
                Text(text = "¿El usuario ha finalizado la tarea?")
            }
        },
        confirmButton = {
            Button(onClick = {
                onFinalizarTareaClick(publicacionId)
                onDismiss() // Cerrar el popup
            }) {
                Text(text = "Finalizar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )

    if (finalizacionExitosa) {
        Toast.makeText(
            LocalContext.current,
            "Finalización exitosa",
            Toast.LENGTH_SHORT
        ).show()
    }
}


@Composable
fun ProfileHeader(
    onProfilePictureClicked: () -> Unit,
    viewModel: ProfileViewModel
) {
    val userData by viewModel.userData.collectAsState()
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color.White, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable(onClick = onProfilePictureClicked)
        ) {
            userData?.let { user ->
                val profilePictureUrl = remember(user.id) {
                    mutableStateOf<String?>(null)
                }
                LaunchedEffect(user.id) {
                    profilePictureUrl.value = user.id?.let { getPFP(it) }
                }

                profilePictureUrl.value?.let { url ->
                    Image(
                        painter = rememberImagePainter(data = url),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "${userData?.nombre} ${userData?.apellido1}",
            style = MaterialTheme.typography.h4
        )

    }
    Spacer(modifier = Modifier.width(7.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color.White, CircleShape)
    ) {
        Text(
            text = " Username : ${userData?.username}",
            style = MaterialTheme.typography.h4
        )
    }
}

suspend fun getPFP(uid: String): String = suspendCoroutine { c ->

    val pfp = Firebase.storage.reference.child("usuarios/${uid}/pfp.png")
    if (Firebase.auth.uid != null) {
        pfp.downloadUrl
            .addOnSuccessListener { url ->
                c.resume(url.toString())
            }
            .addOnFailureListener { e ->
                Log.w("User", "No hay PFP", e)
                c.resume("https://s.tmimgcdn.com/scr/800x500/333400/abeja-panal-animal-logo-diseno-plantilla-vector-v18_333446-original.jpg")


            }
    } else {
        Log.e("User", "Error en la sesión")
        c.resume("https://s.tmimgcdn.com/scr/800x500/333400/abeja-panal-animal-logo-diseno-plantilla-vector-v18_333446-original.jpg")
    }
}


@Composable
fun ProfileInfo(viewModel: ProfileViewModel) {
    val userData by viewModel.userData.collectAsState()
    val cornerSize = 6.dp // Tamaño de curvatura de las esquinas

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 9.dp)
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
            text = " " + "Sobre mí",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        )
        Text(
            text = " " + "${userData?.descripcion}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
        Text(
            text = " " + "Información de contacto",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        )
        Text(
            text = " " + "Email: ${userData?.email}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
        Text(
            text = " " + "Teléfono: ${userData?.telefono}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}


data class Job(
    val id: String,
    val title: String,
    val description: String,
    val price: Double
)


enum class DelegarPopupState {
    None, // Estado inicial
    Success, // Delegación exitosa
    Error // Error en la delegación
}