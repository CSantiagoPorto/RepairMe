package com.example.repairme.data.repository

import com.example.repairme.data.model.Tecnico
import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TecnicoRepository: OperationsTemplateRepository() {

    private val auth = FirebaseAuth.getInstance()
    private val NODE = "employees"

    //Necesitamos grabar un equipo. Cada equipo tiene que ir vinculado a un id de usuario
    //Voy a neceesitar: crearlo, que me devuelva el id, que tenga asociado idUser

    fun crearTecnico(
        tecnico: Tecnico, //En vez de meterle mil parámetros le meto el objeto ya
        error: (String) -> Unit,
        exito: (String) -> Unit
    ) {


        //Problema: HAY QUE GENERAR EL ID y pushearlo
        val tecnicoUid = newId(NODE)

        if (tecnicoUid.isBlank()) {
            error("Se produjo un error al creae el id del dispositivo")
            return
        }

        var tecnicoId = tecnico.copy(
            tecnicoId = tecnicoUid,

        )

        setValue("$NODE/$tecnicoUid", tecnicoId,
            ok = { exito(tecnicoUid) },
            error = { error("Algo pasó y no se grabó en la bbdd") }
        )
    }




    //Necesito obtener todos los usuarios con el role técnico
    fun obtenerTecnicos(
        fallo:(String)->Unit,
        exito:(List<Usuario>)->Unit
    ){
        var listaTecnicos= mutableListOf<Usuario>()
        val tecnicoRef= ref(NODE)
//Tecnicos está en otro nodo
        tecnicoRef.orderByChild("role").equalTo("TECNICO")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children){
                        val uidTecnico = child.key ?: ""
                        val tecnico = child.getValue(Usuario::class.java)

                        if (tecnico != null){
                            listaTecnicos.add(tecnico.copy(id = uidTecnico))
                            //Aquí hay que ponerle el uid porque no se guarda como documento
                        }
                    }
                    exito(listaTecnicos)
                }

                override fun onCancelled(error: DatabaseError) {
                    fallo("No se ha podido recuperar la lista de técnicos disponibles")
                }
            })
    }
    //Editar técnico completo
    fun editarTecnico(
        tecnicoEditado: Usuario,
        fallo: (String) -> Unit,
        exito: () -> Unit
    ) {
        if (tecnicoEditado.id.isBlank()) {
            fallo("El técnico no tiene id")
            return
        }

        setValue("$NODE/${tecnicoEditado.id}", tecnicoEditado,
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }

    //Editar algunos campos del técnico sin reescribir todo el objeto
    fun editarTecnicoParcial(
        tecnicoId: String,
        updates: Map<String, Any?>,
        fallo: (String) -> Unit,
        exito: () -> Unit
    ) {
        if (tecnicoId.isBlank()) {
            fallo("El técnico no tiene id")
            return
        }

        updateChildren("$NODE/$tecnicoId", updates,
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }

    //Eliminar técnico
    fun eliminarTecnico(
        tecnicoId: String,
        fallo: (String) -> Unit,
        exito: () -> Unit
    ) {
        if (tecnicoId.isBlank()) {
            fallo("El técnico no tiene id")
            return
        }

        delete("$NODE/$tecnicoId",
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }
}