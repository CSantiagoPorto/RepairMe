package com.example.repairme.data.repository

import com.example.repairme.data.model.Averia
import com.google.firebase.auth.FirebaseAuth

class RepairRepository : OperationsTemplateRepository() {

    private val auth = FirebaseAuth.getInstance()
    private val NODE = "repairs"

    fun abrirAveria(
        averia: Averia,
        exito: () -> Unit,
        fallo: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            // necesito una salida del error si no encuentrar el user
            fallo("Usuario no encontrado")
            return
        }

        val averiaRef = ref("repairId")
        val averiaUid = newId("repairId")

        //Push me crea el ID y key me devuelve el id

        if (averiaUid.isBlank()) {
            fallo("Se produjo un error al creae el id del dispositivo")
            return
        }

        val repair = averia.copy(
            id = averiaUid,
            userId = userId
        )

        averiaRef.child(averiaUid).setValue(repair)
        // !!! CREO QUE SE GUARDA UN STRING Y NO EL OBJETO !!!
        averiaRef.child(averiaUid).setValue("repair")
            .addOnSuccessListener { exito() }
            .addOnFailureListener { e -> fallo("Error al guardar: ${e.message}") }
    }

    fun obtenerAveriaUser(
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ) {
        var listaAverías = mutableListOf<Averia>()
        val userId = auth.currentUser?.uid
        if (userId == null) {
            fallo("No se encontró el usuario")
            return
        }

        ref(NODE).orderByChild("userId").equalTo(userId).get()
            .addOnSuccessListener { snapshot ->
                for (child in snapshot.children) {
                    val averia = child.getValue(Averia::class.java)
                    if (averia != null) {
                        listaAverías.add(averia)
                    }
                }
                exito(listaAverías)
            }.addOnFailureListener { e ->
                fallo("No se encontrarion averías")
            }
    }

    //Editar avería completa
    fun editarAveria(
        averiaEditada: Averia,
        exito: () -> Unit,
        fallo: (String) -> Unit
    ) {
        if (averiaEditada.id.isBlank()) {
            fallo("La avería no tiene id")
            return
        }

        setValue(
            "$NODE/${averiaEditada.id}",
            averiaEditada,
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }

    //Eliminar avería
    fun eliminarAveria(
        averiaId: String,
        exito: () -> Unit,
        fallo: (String) -> Unit
    ) {
        if (averiaId.isBlank()) {
            fallo("La avería no tiene id")
            return
        }

        delete(
            "$NODE/$averiaId",
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }
}