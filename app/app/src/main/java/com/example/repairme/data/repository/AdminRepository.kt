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

class AdminRepository(private val context: Context) : OperationsTemplateRepository(){
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

        val opciones = FirebaseOptions.Builder()
            .setApplicationId("1:494111396660:android:c664cf2d51e5747160cb61")
            .setApiKey("AIzaSyA3EJzJZELSevtog0YP8vMkFjIT2win6IA")
            .setDatabaseUrl("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")
            .setProjectId("repairme-956fd")
            .setStorageBucket("repairme-956fd.firebasestorage.app")
            .setGcmSenderId("494111396660")
            .build()
        val appSecundaria = try {
            FirebaseApp.getInstance("secundaria")
        } catch (e: Exception) {
            FirebaseApp.initializeApp(context,opciones, "secundaria")
        }
        val authSecundaria = FirebaseAuth.getInstance(appSecundaria)

        authSecundaria.createUserWithEmailAndPassword(email,java.util.UUID.randomUUID().toString()).addOnSuccessListener {
            resultado->
            val uid = resultado.user!!.uid
            //Vamos a mandar un mail de reseteo
            authSecundaria.sendPasswordResetEmail(email)
            authSecundaria.signOut()//Hay que cerrarla y borrar la app
            appSecundaria.delete()
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
            setValue(uid, usuario,
                ok = { exito(uid) },
                error = { msg -> error(msg) }
            )
        }.addOnFailureListener {
            fallo->
            error(fallo.message?: "Error al crear el admin un nuevo usuario")
        }




    }
}