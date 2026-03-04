package com.example.repairme.data.repository

import com.example.repairme.data.model.Averia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RepairRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance(
        "https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app"
    )
    val averiaRef= database.getReference("repairs")

    fun abrirAveria(
        averia: Averia,
        exito:()-> Unit,
        fallo:(String)->Unit

    ){
        val userId= auth.currentUser?.uid
        if (userId==null){
            // necesito una salida del error si no encuentrar el user
            error("Usuario no encontrado")
            return

        }
        val averiaUid= averiaRef.push().key

        //Push me crea el ID y key me devuelve el id

        if(averiaUid==null){
            fallo("Se produjo un error al crear el id de la avería")
            return

        }

        val repair= averia.copy(
            id=averiaUid,
            userId= userId
        )

        averiaRef.child(averiaUid).setValue(repair)
            .addOnSuccessListener { exito() }
            .addOnFailureListener{ e-> fallo("Error al guardar: ${e.message}")}
    }



}