package com.example.repairme.data.repository

import android.widget.Toast
import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.repairme.data.model.EstadoTecnico

class AuthRepository {
    // Esta clase se ocupa únicamente de Firebase.
    // Aquí no va nada de la UI.

    // Usamos 'by lazy' para evitar que Firebase se inicialice durante las Compose Previews,
    // lo que causaría un IllegalStateException ya que FirebaseApp no está inicializado en el proceso de preview.
    private val autenticacion by lazy { FirebaseAuth.getInstance() }
    private val bbdd by lazy {
        FirebaseDatabase.getInstance("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")
    }

    val duration = Toast.LENGTH_LONG

    fun validarCorreoPassword(
        correo: String,
        contraseña: String,
        validacionOK: (Usuario) -> Unit,
        validacionError: (String) -> Unit
    ) {
        autenticacion.signInWithEmailAndPassword(correo, contraseña).addOnSuccessListener { result ->
            val id = result.user?.uid
            if (id != null) {
                bbdd.getReference("users").child(id).get().addOnSuccessListener { snapshot ->
                    val usuario = snapshot.getValue(Usuario::class.java)
                    if (usuario != null) {
                        if(usuario.estado== "Inactivo"){
                            autenticacion.signOut()
                            validacionError("Su cuenta ha sido desactivada. Si desea reactivarla pónase en contacto con el administrador")

                        }else{validacionOK(usuario.copy(id = id))}

                    } else {
                        validacionError("No se encontró el usuario")
                    }
                }.addOnFailureListener { e ->
                    validacionError("Fallo en base de datos: ${e.message}")
                }
            }
        }.addOnFailureListener { e ->
            validacionError("Fallo el login: ${e.message}")
        }
    }

    fun crearUsuario(
        email: String,
        password: String,
        nombre: String,
        apellidos: String,
        telefono: String,
        direccion: String,
        codigoPostal: String,
        localidad: String,
        dni: String,
        role: String = "user",
        creadoOK: () -> Unit,
        creadoError: (String) -> Unit
    ) {
        autenticacion.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val uid = autenticacion.currentUser?.uid ?: return@addOnSuccessListener
            val usuario = Usuario(
                id = uid,
                name = nombre,
                apellidos = apellidos,
                email = email,
                phone = telefono,
                direccion = direccion,
                codigoPostal = codigoPostal,
                localidad = localidad,
                dni = dni,
                role = role,
                createdAt = System.currentTimeMillis(),
                // si es tecnico lo creamos como ACTIVO, si no se crea vacío
                estado = if (role.lowercase() == "tecnico") EstadoTecnico.Activo.name else ""
            )
            bbdd.getReference().child("users").child(uid).setValue(usuario).addOnSuccessListener {
                creadoOK()
            }.addOnFailureListener { e -> creadoError("Error de BD: ${e.message}") }
        }.addOnFailureListener { e ->
            creadoError("Error al crear usuario: ${e.message}")
        }
    }
}
