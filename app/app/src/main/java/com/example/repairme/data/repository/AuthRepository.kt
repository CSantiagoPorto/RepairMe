package com.example.repairme.data.repository

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.repairme.ui.screens.auth.LoginScreen

class AuthRepository {
    //Esta clase se ocupa únicamente de FireBase.
    //Aquí no va nada de la UI
    private var autenticacion = FirebaseAuth.getInstance()
    private val bbdd =
        FirebaseDatabase.getInstance("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")

    val duration = Toast.LENGTH_LONG


    fun validarCorreoPassword(
        correo: String,
        contraseña: String,
        validacionOK: (Usuario) -> Unit,
        validacionError: (String) -> Unit


    ) {
        autenticacion.signInWithEmailAndPassword(correo, contraseña).addOnSuccessListener {
            //Esto hay que hacerlo asíncrono por lo tanto no nos vale un if
            //Por tanto hay que usar CallBacks
                result ->
            val id = result.user?.uid
            if (id != null) {
                bbdd.getReference("users").child(id).get().addOnSuccessListener {
                    // datos.child("name").getValue(String::class.java)
                        snapshot ->
                    val usuario = snapshot.getValue(Usuario::class.java)
                    if (usuario != null) {
                        validacionOK(usuario.copy(id = id))


                    } else {
                        validacionError("No se encontró")
                    }
                }.addOnFailureListener { e ->
                    validacionError("falló bbdd: ${e.message}")
                }

            }


        }.addOnFailureListener { e ->
            validacionError("falló el login: ${e.message}")
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

                result ->
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
                createdAt = System.currentTimeMillis()
            )
            bbdd.getReference().child("users").child(uid).setValue(usuario).addOnSuccessListener {
                creadoOK()
            }.addOnFailureListener { e -> creadoError("Error de bd: ${e.message}") }
        }
    }



}