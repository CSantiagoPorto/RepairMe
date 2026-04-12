package com.example.repairme.data.repository

import com.example.repairme.data.model.Equipo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DeviceRepository : OperationsTemplateRepository() {

    private val auth = FirebaseAuth.getInstance()
    private val NODE = "devices"

    //Necesitamos grabar un equipo. Cada equipo tiene que ir vinculado a un id de usuario
    //Voy a neceesitar: crearlo, que me devuelva el id, que tenga asociado idUser

    fun crearEquipo(
        equipo: Equipo, //En vez de meterle mil parámetros le meto el objeto ya
        error: (String) -> Unit,
        exito: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            // necesito una salida del error si no encuentrar el user
            error("Usuario no encontrado")
            return
        }

        //Problema: HAY QUE GENERAR EL ID y pushearlo
        val dispositivoUid = newId(NODE)

        if (dispositivoUid.isBlank()) {
            error("Se produjo un error al creae el id del dispositivo")
            return
        }

        var dispositivo = equipo.copy(
            devicesId = dispositivoUid,
            userId = userId
        )

        setValue("$NODE/$dispositivoUid", dispositivo,
            ok = { exito(dispositivoUid) },
            error = { error("Algo pasó y no se grabó en la bbdd") }
        )
    }

    fun obtenerEquipos(
        error: (String) -> Unit,
        exito: (List<Equipo>) -> Unit,//Me tiene que devolver la lista en caso de éxito
    ) {
        var listaEquipos = mutableListOf<Equipo>()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            error("Se produjo un error al encontrar el id de usuario")
            return
        }

        //esto funciona ordenando por ese hijo, busca los que son de ese user y obtiene el objeto
        ref(NODE).orderByChild("userId").equalTo(userId).get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                val equipo = child.getValue(Equipo::class.java)
                if (equipo != null) {
                    val equipoConId = equipo.copy(devicesId = child.key ?: "")
                    listaEquipos.add(equipoConId)
                }
            }
            exito(listaEquipos)
        }.addOnFailureListener { e ->
            error("Algo pasó y no se pudieron recuperar los equipos")
        }
    }

    //Para editar el equipo completo (reescribe el objeto Equipo)
    fun editarEquipo(
        equipo: Equipo,
        error: (String) -> Unit,
        exito: () -> Unit
    ) {
        val dispositivoUid = equipo.devicesId
        if (dispositivoUid.isBlank()) {
            error("El equipo no tiene id (devicesId)")
            return
        }

        setValue("$NODE/$dispositivoUid", equipo,
            ok = { exito() },
            error = { msg -> error(msg) }
        )
    }

    //Edita algunos campos sin editar todo el objeto
    fun editarEquipoParcial(
        devicesId: String,
        updates: Map<String, Any?>,
        error: (String) -> Unit,
        exito: () -> Unit
    ) {
        if (devicesId.isBlank()) {
            error("El equipo no tiene id (devicesId)")
            return
        }

        updateChildren("$NODE/$devicesId", updates,
            ok = { exito() },
            error = { msg -> error(msg) }
        )
    }

    //Borra el equipo/nodo
    fun eliminarEquipo(
        devicesId: String,
        error: (String) -> Unit,
        exito: () -> Unit
    ) {
        if (devicesId.isBlank()) {
            error("El equipo no tiene id (devicesId)")
            return
        }

        delete("$NODE/$devicesId",
            ok = { exito() },
            error = { msg -> error(msg) }
        )
    }

    //Para detectar cambios a tiempo real en el listado de equipos del usuario
    fun escucharEquiposUser(
        onData: (List<Equipo>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("Se produjo un error al encontrar el id de usuario")
            return
        }

        ref(NODE).orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lista = snapshot.children.mapNotNull { it.getValue(Equipo::class.java) }
                    onData(lista)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }
    fun crearEquipoAdmin(
        equipo: Equipo, //En vez de meterle mil parámetros le meto el objeto ya
        error: (String) -> Unit,
        exito: (String) -> Unit,
        userId: (String)
    ){


        val dispositivoUid = newId(NODE)

        if (dispositivoUid.isBlank()) {
            error("Se produjo un error al creae el id del dispositivo")
            return
        }

        var dispositivo = equipo.copy(
            devicesId = dispositivoUid,
            userId = userId
        )

        setValue("$NODE/$dispositivoUid", dispositivo,
            ok = { exito(dispositivoUid) },
            error = { error("Algo pasó y no se grabó en la bbdd") }
        )
    }


}