package com.example.helphive1.model
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.UserData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DBGestor {


    private val firebaseDatabase = FirebaseFirestore.getInstance()

    // Reference to the Firebase Firestore
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    private val collection = db.collection("usuarios")

    companion object {
        @Volatile private var instance: DBGestor? = null

        fun getInstance(): DBGestor {
            return instance ?: synchronized(this) {
                instance ?: DBGestor().also { instance = it }
            }
        }
    }

    suspend fun getUserData(id: String): UserData? = withContext(Dispatchers.IO) {
        val document = collection.document(id).get().await()
        document.toObject(UserData::class.java)
    }

    fun uploadNewPfp(pfp: File, userId: String, callback: (success: Boolean) -> Unit) {
        val storageRef = Firebase.storage.reference.child("usuarios/$userId/pfp.png")

        val uploadTask = storageRef.putFile(Uri.fromFile(pfp))
        uploadTask.addOnSuccessListener {
            callback(true) // Carga exitosa
        }.addOnFailureListener { exception ->
            callback(false) // Error en la carga
        }
    }


    fun updateDescription(newDescription: String, id: String) {
        val document = collection.document(id)
        document.update("descripcion", newDescription)
            .addOnSuccessListener {
                // La descripción se ha actualizado exitosamente
            }
            .addOnFailureListener {
                // Ha ocurrido un error al actualizar la descripción
            }
    }

    fun updateUserProfilePictureUrl(newProfilePictureUrl: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // If the user is not logged in, return an error
        if (userId == null) {
            onFailure("User not logged in.")
            return
        }

        // Update the user's profile picture URL in the database
        db.collection("users").document(userId).update("profilePictureUrl", newProfilePictureUrl)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "An error occurred while updating the profile picture URL in the database.")
            }
    }

    fun signInWithEmailPassword(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Authentication failed.")
            }
    }

    fun registerUser(username: String,nombre : String, apellido1 : String, apellido2: String , telefono : String , localidad: String, email: String, password: String,descri : String,ofrecidos : Int, realizados : Int, onComplete: () -> Unit, onError: (String) -> Unit) {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {

                    // El registro fue exitoso
                    val firebaseUser = firebaseAuth.currentUser
                    val user = Usuario(
                        id = firebaseUser?.uid ?: "",
                        username = username,
                        nombre = nombre,
                        apellido1 = apellido1,
                        apellido2 = apellido2,
                        telefono = telefono,
                        localidad = localidad,
                        email = email,
                        descripcion = descri,
                        profilePictureUrl = "",
                        ofrecidos = 0,
                        realizados = 0

                    )

                    firebaseUser?.let {

                        firebaseDatabase.collection("usuarios").document(firebaseUser!!.uid)
                            .set(user)
                        onComplete()
                    }

                } else {
                    // El registro falló
                    onError("Registro falló")
                }
            }
    }
}