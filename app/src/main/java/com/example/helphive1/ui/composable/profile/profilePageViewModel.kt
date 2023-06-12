package com.example.helphive1.ui.composable.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.helphive1.model.DBGestor
import com.example.helphive1.model.Publication
import com.example.helphive1.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.UserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
sealed class UpdateDescriptionStatus {
    object Success : UpdateDescriptionStatus()
    data class Error(val message: String) : UpdateDescriptionStatus()
}

class ProfileViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    private val publicationsCollection = firestore.collection("publicaciones")
    private val usuariosCollection = firestore.collection("usuarios")

    private val _userOfrecidos = MutableLiveData<Int>()
    val userOfrecidos: LiveData<Int> get() = _userOfrecidos

    private val _userRealizados = MutableLiveData<Int>()
    val userRealizados: LiveData<Int> get() = _userRealizados

    fun getUserOfrecidos(userId: String) {
        usuariosCollection.document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    _userOfrecidos.value = usuario?.ofrecidos ?: 0
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error en caso de fallo al obtener los datos del usuario
            }
    }

    fun getUserRealizados(userId: String) {
        usuariosCollection.document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    _userRealizados.value = usuario?.realizados ?: 0
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error en caso de fallo al obtener los datos del usuario
            }
    }

    private val _userData = MutableStateFlow<Usuario?>(null)
    val userData: StateFlow<Usuario?> = _userData

    fun getUserDataById(userId: String) {
        // Aquí realizas la lógica para obtener los datos del usuario según su ID
        // y luego actualizas el valor de _userData
        // Por ejemplo:
        val firestore = Firebase.firestore
        val usuariosCollection = firestore.collection("usuarios")
        val userRef = usuariosCollection.document(userId)

        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val usuario = documentSnapshot.toObject(Usuario::class.java)
                _userData.value = usuario
            }
        }.addOnFailureListener { exception ->
            // Manejar el error en caso de fallo al obtener los datos del usuario
        }
    }

    fun updateDescription(newDescription: String, userId: String) {
        usuariosCollection.document(userId)
            .update("descripcion", newDescription)
            .addOnSuccessListener {
                Log.d("ProfileViewModel", "Description updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileViewModel", "Error updating description", exception)
            }
    }

    private val _userPublications = MutableLiveData<List<Publication>>()
    val userPublications: LiveData<List<Publication>> get() = _userPublications

    fun loadUserPublications(userId: String) {
        val query = publicationsCollection.whereEqualTo("autorId", userId)

        query.addSnapshotListener { querySnapshot, _ ->
            val publicationList = querySnapshot?.toObjects(Publication::class.java)
            _userPublications.value = publicationList ?: emptyList()
        }
    }


}
