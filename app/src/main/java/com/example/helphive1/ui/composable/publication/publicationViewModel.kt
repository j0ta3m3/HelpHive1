import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.helphive1.model.Publication
import com.example.helphive1.model.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.Normalizer

class PublicacionViewModel(private val onDelegarClick: ((String) -> Unit)? = null,private val onFinalizarClick: ((String) -> Unit)? = null) : ViewModel() {
    private val repository: PublicacionRepository = PublicacionRepository()

    private val _publications = MutableLiveData<List<Publication>>()
    val publications: LiveData<List<Publication>> get() = _publications

    private val publicacionesRef = Firebase.firestore.collection("publicaciones")

    private val usersRef = Firebase.firestore.collection("usuarios")


    fun verificarUsuarioEnBaseDeDatos(username: String): Boolean {
        val userId = runBlocking {
            repository.obtenerIdUsuarioPorUsername(username)
        }

        return userId != null
    }

    suspend fun actualizarRealizadosUsuario(userId: String, cantidad: Int) {
        withContext(Dispatchers.IO) {
            val userRef = usersRef.document(userId)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(Usuario::class.java)
                user?.let {
                    val nuevosRealizados = user.realizados + cantidad
                    userRef.update("realizados", nuevosRealizados)
                        .addOnSuccessListener {
                            // Actualización exitosa del campo "realizados" del usuario
                            Log.d(TAG, "Actualización exitosa del campo 'realizados' del usuario")

                        }
                        .addOnFailureListener { e ->
                            // Manejo del error de actualización del campo "realizados" del usuario
                            Log.e(TAG, "Error al actualizar el campo 'realizados' del usuario: ${e.message}")
                        }
                }
            }
                .addOnFailureListener { e ->
                    // Manejo del error de obtención del documento del usuario
                    Log.e(TAG, "Error al obtener el documento del usuario: ${e.message}")
                }
        }
    }


    fun finalizarTarea(publicacionId: String) {
        viewModelScope.launch {
            val publicacion = withContext(Dispatchers.IO) {
                obtenerPublicacion(publicacionId)
            }

            if (publicacion != null) {
                publicacion.estado = "Realizado"
                withContext(Dispatchers.IO) {
                    guardarPublicacion(publicacion)
                }

                // Obtener el id del usuario asignado a la publicación
                val userId = publicacion.idWorker

                if (userId != null) {
                    // Incrementar el campo "realizados" del usuario
                    val cantidadRealizados = 1 // Valor a sumar
                    withContext(Dispatchers.IO) {
                        actualizarRealizadosUsuario(userId, cantidadRealizados)
                    }
                }
            }
        }
    }

    fun delegarTarea(publicacionId: String, username: String) {
        viewModelScope.launch {
            val userId = withContext(Dispatchers.IO) {
                repository.obtenerIdUsuarioPorUsername(username)
            }

            if (userId != null) {
                val publicacion = withContext(Dispatchers.IO) {
                    obtenerPublicacion(publicacionId)
                }

                if (publicacion != null) {
                    publicacion.idWorker = userId
                    publicacion.estado = "Inprogress"
                    withContext(Dispatchers.IO) {
                        guardarPublicacion(publicacion)
                    }
                }
            }
        }
    }


    private suspend fun obtenerPublicacion(publicacionId: String): Publication? {
        return withContext(Dispatchers.IO) {
            // Lógica para obtener la publicación correspondiente por su ID

            try {
                val document = publicacionesRef.document(publicacionId).get().await()
                val publicacion = document.toObject(Publication::class.java)
                return@withContext publicacion
            } catch (e: Exception) {
                // Manejo del error de obtención de la publicación
                return@withContext null
            }
        }
    }

    private suspend fun guardarPublicacion(publicacion: Publication) {
        return withContext(Dispatchers.IO) {
            // Lógica para guardar los cambios de la publicación en la base de datos o servicio
            try {
                publicacionesRef.document(publicacion.id).set(publicacion).await()
            } catch (e: Exception) {
            }
        }
    }


    fun loadPublicaciones(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getPublicaciones(
            onSuccess = { publicationList ->
                _publications.value = publicationList
                onSuccess()
            },
            onFailure = { errorMessage ->
                onFailure(errorMessage)
            }
        )
    }

    fun addPublicacion(publicacion: Publication, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.addPublicacion(publicacion,
            onSuccess = { onSuccess() },
            onFailure = { errorMessage -> onFailure(errorMessage) }
        )
    }


}

class PublicacionRepository {
    private val publicacionesRef = Firebase.firestore.collection("publicaciones")
    private val usersRef = Firebase.firestore.collection("usuarios")


    suspend fun obtenerIdUsuarioPorUsername(username: String): String? {
        return withContext(Dispatchers.IO) {
            val querySnapshot = usersRef
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val user = querySnapshot.documents[0]
                return@withContext user.id
            } else {
                return@withContext null
            }
        }
    }

    suspend fun obtenerPublicacion(publicacionId: String): Publication? {
        return withContext(Dispatchers.IO) {
            val document = publicacionesRef.document(publicacionId).get().await()
            val publicacion = document.toObject(Publication::class.java)
            return@withContext publicacion
        }
    }

    suspend fun guardarPublicacion(publicacion: Publication) {
        return withContext(Dispatchers.IO) {
            publicacionesRef.document(publicacion.id).set(publicacion).await()
        }
    }

    fun getPublicaciones(
        onSuccess: (List<Publication>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        publicacionesRef.whereEqualTo("estado", "Disponible")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val publicationList = querySnapshot.toObjects(Publication::class.java)
                onSuccess(publicationList)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error al obtener las publicaciones")
            }
    }

    suspend fun searchPublicationsByLocation(location: String): List<Publication> {
        val normalizedLocation = normalizeString(location)

        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = publicacionesRef
                    .get()
                    .await()

                val publicationList = querySnapshot.documents
                    .mapNotNull { document ->
                        val publication = document.toObject(Publication::class.java)
                        if (publication != null && normalizeString(publication.localizacion) == normalizedLocation) {
                            return@mapNotNull publication
                        }
                        return@mapNotNull null
                    }

                return@withContext publicationList
            } catch (e: Exception) {
                // Manejo del error de búsqueda
                return@withContext emptyList()
            }
        }
    }

    private fun normalizeString(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        return normalized.replace("\\p{M}".toRegex(), "")
    }

    fun getPublicacionesByPartialName(
        partialName: String,
        onSuccess: (List<Publication>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        publicacionesRef.whereGreaterThanOrEqualTo("titulo", partialName)
            .whereLessThan("titulo", partialName + "\uf8ff")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val publicationList = querySnapshot.toObjects(Publication::class.java)
                onSuccess(publicationList)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error al obtener las publicaciones")
            }
    }

    fun addPublicacion(publicacion: Publication, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val publicacionData = hashMapOf(
            "id" to publicacion.id,
            "titulo" to publicacion.titulo,
            "descripcion" to publicacion.descripcion,
            "categoria" to publicacion.categoria,
            "remuneracion" to publicacion.remuneracion,
            "autorId" to publicacion.autorId,
        "localizacion" to publicacion.localizacion,
            "estado" to publicacion.estado,
            "idWorker" to publicacion.idWorker
        )

        publicacionesRef.document(publicacion.id)
            .set(publicacionData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error al añadir la publicación") }
    }

    fun searchPublicationsByName(
        name: String,
        onSuccess: (List<Publication>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        publicacionesRef.whereEqualTo("titulo", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val publicationList = querySnapshot.toObjects(Publication::class.java)
                onSuccess(publicationList)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error al buscar las publicaciones por nombre")
            }
    }
}
