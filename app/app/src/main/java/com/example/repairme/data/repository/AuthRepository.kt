package com.example.repairme.data.repository

import android.R
import android.widget.Toast
import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.repairme.data.model.EstadoTecnico
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts



class AuthRepository {
    // Esta clase se ocupa únicamente de Firebase.
    // Aquí no va nada de la UI.

    // Usamos 'by lazy' para que no se rompa con la vista previa
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

    fun loginGoogle(
        tokenGoogle: String,//A fb hay que pasarle el token porque no sabe lo que ha estado haciendo google
        ok: (Usuario)-> Unit,
        fallo: (String)-> Unit
    ){//credencialGoogle va a guardar la credencial de google porque fb necesita tenerla entendible
        val credencialGoogle= GoogleAuthProvider.getCredential(tokenGoogle, null)//No necesitamos el accessToken así que lo pongo a null porque me pide los dos parámetros
        autenticacion.signInWithCredential(credencialGoogle)//le mandamos la credencial a fb
            .addOnSuccessListener {
                resultado->
                var id= resultado.user?.uid ?: return@addOnSuccessListener
                // le coge el uid de Firebase Si es null se sale
                bbdd.getReference("users").child(id).get().addOnSuccessListener {
                    snapshot ->
                    var usuario=snapshot.getValue(Usuario::class.java)
                    if(usuario!= null){
                        //Si tiene la cuenta desactivada no le deja entrar tampoco con Google
                        if(usuario.estado=="Inactivo"){
                            autenticacion.signOut()
                               fallo("Su cuenta ha sido desactivada")
                        }else{
                            ok(usuario.copy(id=id))
                        }
                    }else{
                        //Si no existe su usuario con ese token lo va a crear
                        val nuevoUsuarioGoogle= Usuario(
                            id = id,
                            name = resultado.user?.displayName?:"",

                            email = resultado.user?.email?:"",

                            role = "user",
                            createdAt = System.currentTimeMillis(),


                        )
                        bbdd.getReference("users").child(id).setValue(nuevoUsuarioGoogle)
                            .addOnSuccessListener { ok(nuevoUsuarioGoogle) }
                            .addOnFailureListener { e-> fallo("No se pudo crear el usuario") }
                    }
                }
            }.addOnFailureListener {
                e-> fallo("Error de bbdd")
            }
    }
}
