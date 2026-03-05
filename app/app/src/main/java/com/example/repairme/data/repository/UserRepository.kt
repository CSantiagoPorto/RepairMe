package com.example.repairme.data.repository

import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserRepository {
    private var autenticacion = FirebaseAuth.getInstance()
    private val bbdd = FirebaseDatabase.getInstance("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")

    //Necesito obtener todos los usuarios con el role técnico
    fun obtenerTecnicos(
        fallo:(String)->Unit,
        exito:(List<Usuario>)->Unit
    ){
        var listaTecnicos= mutableListOf<Usuario>()
        val tecnicoRef= bbdd.getReference("users")

        tecnicoRef.orderByChild("role").equalTo("tecnico").get().addOnSuccessListener {
            snapshot->for (
                child in snapshot.children
            ){

                 val uidTenico= child.key ?: ""
            val tecnico= child.getValue(Usuario::class.java)
                if (tecnico!=null){

                    listaTecnicos.add(tecnico.copy(id=uidTenico))
                    //Aquí hay que ponerle el uid porque no se guarda como documento
                }
            }
            exito(listaTecnicos)

        }.addOnFailureListener(
            { e->fallo ("No se ha podido recuperar la lista de técnicos disponibles")}
        )

    }
}