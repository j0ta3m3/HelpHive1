package com.example.helphive1.ui.composable.home
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.helphive1.model.Publication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _publicaciones = MutableLiveData<List<Publication>>()
    val publicaciones: LiveData<List<Publication>> = _publicaciones

    init {
        getPublicaciones()
    }

    private fun getPublicaciones() {
        db.collection("publicaciones")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val listaPublicaciones = mutableListOf<Publication>()
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val titulo = document.getString("titulo") ?: ""
                    val descripcion = document.getString("descripcion") ?: ""
                    val categoria = document.getString("categoria") ?: ""
                    val remuneracion = document.getDouble("remuneracion") ?: 0.0
                    val autorId = document.getString("autorId") ?: ""
                    val localizacion = document.getString("localizacion") ?: ""
                    val estado = document.getString("estado") ?: ""
                    val publicacion = Publication(id, titulo, descripcion, categoria, remuneracion, autorId,localizacion,estado)
                    listaPublicaciones.add(publicacion)
                }
                _publicaciones.value = listaPublicaciones
                Log.d("HomeViewModel", "Publicaciones obtenidas correctamente. Cantidad: ${listaPublicaciones.size}")
            }
            .addOnFailureListener { exception ->
                // Manejar el error en caso de que la obtención de publicaciones falle
                Log.e("HomeViewModel", "Error al obtener las publicaciones: ${exception.message}", exception)
            }
    }

    fun loadMorePublications() {

    }
}

