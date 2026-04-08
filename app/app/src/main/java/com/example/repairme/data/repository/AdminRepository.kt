package com.example.repairme.data.repository

import android.content.Context
import com.example.repairme.data.model.EstadoTecnico
import com.example.repairme.data.model.Usuario
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseApp.getInstance
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminRepository : OperationsTemplateRepository(){
    private val auth = FirebaseAuth.getInstance()
    private val bbdd by lazy {
        FirebaseDatabase.getInstance("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")
    }
    private val NODE = "users"

    fun crearUsuarioAdmin(
        email: String,
        //password: String,
        nombre: String,
        apellidos: String,
        telefono: String,
        direccion: String,
        codigoPostal: String,
        localidad: String,
        dni: String,
        role: String = "user",
        error: (String) -> Unit,
        exito: (String) -> Unit
    ) {
        val uid= newId(NODE)

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

        setValue("$NODE/${uid}", usuario,
            ok = { exito(uid) },
            error = { msg -> error(msg) }
        )


    }
}