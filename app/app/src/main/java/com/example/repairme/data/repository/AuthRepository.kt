package com.example.repairme.data.repository

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {
    //Esta clase se ocupa únicamente de FireBase.
    //Aquí no va nada de la UI
    private var autenticacion= FirebaseAuth.getInstance()
    private val bbdd= FirebaseDatabase.getInstance( "https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")

    val duration= Toast.LENGTH_LONG




    fun validarCorreoPassword(
        correo: String,
        contraseña:String,
        validacionOK: (String)->Unit,
        //Esto es una función que recibe un String y devuelve un Unit, que es un objeto
        //Que va a ser un objeto vacío. Se usa para avisar que todo ha ido bien. Cuando sale, es cuando
        //puedo lanzar el Toast en Login
        validacionError: (String)->Unit


    ){
        autenticacion.signInWithEmailAndPassword(correo,contraseña).addOnSuccessListener {
            //Esto hay que hacerlo asíncrono por lo tanto no nos vale un if
            //Por tanto hay que usar CallBacks
            result-> val id = result.user?.uid
            if(id!= null){
                bbdd.getReference("users").child(id).get().addOnSuccessListener {
                    datos-> val nombreUser= datos.child("name").getValue(String::class.java)
                    if(!nombreUser.isNullOrBlank()){
                        validacionOK(nombreUser)
                    }else{
                        validacionError("No se encontró")
                    }
                }.addOnFailureListener{e->
                    validacionError("falló bbdd: ${e.message}")
                }

            }


        }.addOnFailureListener{e->
            validacionError("falló el login: ${e.message}")
        }




    }

}