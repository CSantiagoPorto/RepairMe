package com.example.repairme.data.repository

import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Equipo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DeviceRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance(
        "https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app"
    )

    //Necesitamos grabar un equipo. Cada equipo tiene que ir vinculado a un id de usuario
    //Voy a neceesitar: crearlo, que me devuelva el id, que tenga asociado idUser
    
    fun crearEquipo(

        devicesId:String="",
        deviceBrand:String="",
        deviceModel:String="",
        deviceSN:String="",
        userId: String = "",
        averias: List<Averia> = emptyList(),
        error: (String)->Unit,
        exito:(String)->Unit
    ){
        val userId= auth.currentUser?.uid
        if (userId==null){
            // necesito una salida del error si no encuentrar el user
            error("Usuario no encontrado")
            return

        }

        //Problema: HAY QUE GENERAR EL ID y pushearlo
        val dispositivoRef= database.getReference("devices")// esto obtiene la ruta
        val dispositivoUid= dispositivoRef.push().key

        if(dispositivoUid==null){
            error("Se produjo un error al creae el id del dispositivo")
            return

        }
        var dispositivo= Equipo(
            devicesId = devicesId,
            deviceBrand = deviceBrand,
            deviceModel = deviceModel,
            deviceSN = deviceSN,
            userId = userId)
        dispositivoRef.child(dispositivoUid).setValue(dispositivo).addOnSuccessListener {
            exito(dispositivoUid)

        }.addOnFailureListener{
            kotlin.error("Algo pasó y no se grabó en la bbdd")
        }

    }
}