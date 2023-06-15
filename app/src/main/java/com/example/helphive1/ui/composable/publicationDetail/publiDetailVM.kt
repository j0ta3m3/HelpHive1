package com.example.helphive1.ui.composable.publicationDetail

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.helphive1.model.Publication
import com.example.helphive1.model.Usuario
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PublicacionDetalleViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _publicacion = MutableLiveData<Publication>()
    val publicacion: LiveData<Publication> = _publicacion

    private val _autorNombre = MutableLiveData<String>()
    val autorNombre: LiveData<String> = _autorNombre

    private val _autorTelefono = MutableLiveData<String>()
    val autorTelefono: LiveData<String> = _autorTelefono


    fun getPublicacion(publicacionId: String) {
        db.collection("publicaciones")
            .document(publicacionId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val id = document.id
                    val titulo = document.getString("titulo") ?: ""
                    val descripcion = document.getString("descripcion") ?: ""
                    val categoria = document.getString("categoria") ?: ""
                    val remuneracion = document.getDouble("remuneracion") ?: 0.0
                    val autorId = document.getString("autorId") ?: ""
                    val publicacion = Publication(id, titulo, descripcion, categoria, remuneracion, autorId)
                    _publicacion.value = publicacion
                    getAutorInfo(autorId)
                    Log.d("PublicationDetailViewModel", "Publicación obtenida correctamente: $publicacion")
                } else {
                    _publicacion.value = null
                    Log.d("PublicationDetailViewModel", "La publicación no existe")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PublicationDetailViewModel", "Error al obtener la publicación: ${exception.message}", exception)
            }
    }

    private fun getAutorInfo(autorId: String) {
        db.collection("usuarios")
            .document(autorId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("etiqueta","el ID del usuario es : $document")

                    val nombre = document.getString("nombre") ?: ""

                    Log.d("etiqueta","el nombre del usuario es : $nombre")

                    val telefono = document.getString("telefono") ?: ""
                    _autorNombre.value = nombre
                    _autorTelefono.value = telefono


                } else {
                    _autorNombre.value = ""
                    _autorTelefono.value = ""
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PublicationDetailViewModel", "Error al obtener la información del autor: ${exception.message}", exception)
            }
    }
}
