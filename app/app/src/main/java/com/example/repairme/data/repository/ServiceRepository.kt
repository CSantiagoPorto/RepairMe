package com.example.repairme.data.repository

import com.example.repairme.data.model.Servicio
import com.google.firebase.database.*

class ServiceRepository : OperationsTemplateRepository() {

    private val NODE = "services"

    fun obtenerServicios(
        fallo: (String) -> Unit,
        exito: (List<Servicio>) -> Unit
    ) {
        val lista = mutableListOf<Servicio>()

        ref(NODE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val servicio = child.getValue(Servicio::class.java)
                    val id = child.key ?: ""

                    if (servicio != null && servicio.activo) {
                        lista.add(servicio.copy(id = id))
                    }
                }
                exito(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                fallo(error.message)
            }
        })
    }

    fun crearServicio(
        servicio: Servicio,
        fallo: (String) -> Unit,
        exito: () -> Unit
    ) {
        val key = ref(NODE).push().key ?: return

        setValue("$NODE/$key", servicio,
            ok = { exito() },
            error = { fallo(it) }
        )
    }

    fun editarServicio(
        servicio: Servicio,
        fallo: (String) -> Unit,
        exito: () -> Unit
    ) {
        if (servicio.id.isBlank()) {
            fallo("ID vacío")
            return
        }

        setValue("$NODE/${servicio.id}", servicio,
            ok = { exito() },
            error = { fallo(it) }
        )
    }

    fun eliminarServicio(
        id: String,
        fallo: (String) -> Unit,
        exito: () -> Unit
    ) {
        delete("$NODE/$id",
            ok = { exito() },
            error = { fallo(it) }
        )
    }
}