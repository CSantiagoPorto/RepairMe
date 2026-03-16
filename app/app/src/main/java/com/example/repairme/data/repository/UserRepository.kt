package com.example.repairme.data.repository

import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UserRepository : OperationsTemplateRepository() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val NODE = "users"

    //Aquí dejo esto porque el técnico va a necesitar recuperar los usuarios
    //También buscarlos por nombre

    fun obtenerCualquierUsuarioPorId(
        fallo:(String)->Unit,
        exito:(Usuario)->Unit,
        id:String
    ){
        ref("$NODE/$id").get().addOnSuccessListener {
            snapshot->
            val usuario=snapshot.getValue(Usuario::class.java)
            if(usuario!=null){
                exito(usuario)
            } else{
                fallo("No se encontró ningún usuario con ese id")
            }
        }.addOnFailureListener {
            fallo("Error de bbdd para obtener el usuario")
        }
    }




}