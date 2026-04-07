package com.example.repairme.data.repository

import android.util.Log
import com.example.repairme.data.model.Averia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RepairRepository : OperationsTemplateRepository() {

    // Lazy: Solo se inicializa la primera vez
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val NODE = "repairs"

    fun abrirAveria(
        averia: Averia,
        exito: () -> Unit,
        fallo: (String) -> Unit

    ){
        val userId= auth.currentUser?.uid
        if (userId==null){
            fallo("Usuario no encontrado")
            return
        }

        val averiaUid= newId(NODE)

        if(averiaUid.isBlank()){
            fallo("Se produjo un error al crear el id de la avería")
            return
        }

        val repair= averia.copy(
            id=averiaUid,
            userId= userId,
            createdAt = System.currentTimeMillis()
        )

        setValue("$NODE/$averiaUid", repair,
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }

    // El admin necesita todas las averías para poder asignarlas
    fun obtenerAveriasTodas(
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ){
        val listaAverías= mutableListOf<Averia>()
        val averiaRef= ref(NODE)
        //Quiero que me muestre primero las pendientes de asignar, así que hago una lista de estados
        val estado= listOf("Pendiente", "Asignada", "Presupuestada", "En reparación", "reparado")
        averiaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaAverías = mutableListOf<Averia>()
                for (child in snapshot.children) {
                    val averia = child.getValue(Averia::class.java)
                    if (averia != null) {
                        listaAverías.add(if (averia.id.isEmpty()) averia.copy(id = child.key ?: "") else averia)
                    }
                }
                exito(listaAverías.sortedBy { estado.indexOf(it.estado) })
            }
            override fun onCancelled(error: DatabaseError) {
                fallo(error.message)
            }
        })
    }

    // CAMBIO: Ahora usa addValueEventListener para que el usuario vea cambios en tiempo real
    fun obtenerAveriaUser(
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ){
        val listaAverías= mutableListOf<Averia>()
        val userId= auth.currentUser?.uid
        val averiaRef= ref(NODE)
        Log.d("TESTCRUD", "obtenerAveriaUser llamado (Realtime), userId=$userId")
        if(userId==null){
            fallo("No se encontró el usuario")
            return
        }

        averiaRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaAverías = mutableListOf<Averia>()
                    for(child in snapshot.children){
                        val averia = child.getValue(Averia::class.java)
                        if(averia != null){
                            val averiaConId = if (averia.id.isEmpty()) averia.copy(id = child.key ?: "") else averia
                            listaAverías.add(averiaConId)
                        }
                    }
                    exito(listaAverías)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("TESTCRUD", "onCancelled: ${error.message}")
                    fallo(error.message)
                }
            })
    }

    fun obtenerAveriaId(
        fallo: (String) -> Unit,
        exito: (Averia) -> Unit,
        averiaId:String
    ){
        val averiaRef= ref("$NODE/$averiaId")
        averiaRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                fallo("Nodo no existe. ID buscado: '$averiaId'")
                return@addOnSuccessListener
            }
            val averia = snapshot.getValue(Averia::class.java)
            if(averia != null){ exito(averia) }
            else fallo("El nodo existe pero no se pudo deserializar")
        }.addOnFailureListener {
           e->fallo("Algo pasó ")
       }
    }

    // Obtener Averia tecnico en tiempo real
    fun obtenerAveriasTecnico(
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ) {
        val tecnicoId = auth.currentUser?.uid
        val averiaRef= ref(NODE)

        if (tecnicoId == null) {
            fallo("No se encontró el técnico")
            return
        }

        // Pongo addValueEventListener para que me devuelva los cambios en tiempo real
        averiaRef.orderByChild("tecnicoId").equalTo(tecnicoId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaAverias= mutableListOf<Averia>()
                    // Usamos el for para recorrer los hijos
                    for (child in snapshot.children) {
                        val averia = child.getValue(Averia::class.java)
                        if (averia != null) {
                            listaAverias.add(if (averia.id.isEmpty()) averia.copy(id = child.key ?: "") else averia)
                        }
                    }
                    exito(listaAverias)
                }

                override fun onCancelled(error: DatabaseError) {
                    fallo(error.message)
                }
            })
    }

    //Editar avería completa
    fun editarAveria(
        averiaEditada:Averia,
        exito:()->Unit,
        fallo:(String)->Unit
    ){
        if(averiaEditada.id.isBlank()){
            fallo("La avería no tiene id")
            return
        }

        // Obtener la avería anterior para comparar el estado
        ref("$NODE/${averiaEditada.id}").get().addOnSuccessListener { snapshot ->
            val averiaAnterior = snapshot.getValue(Averia::class.java)

            // Actualizar la avería
            setValue("$NODE/${averiaEditada.id}", averiaEditada,
                ok = {
                    // Si el estado cambió, enviar notificación al cliente
                    if (averiaAnterior != null && averiaAnterior.estado != averiaEditada.estado) {
                        enviarNotificacionCambioEstado(averiaEditada, averiaAnterior.estado, averiaEditada.estado)
                    }
                    exito()
                },
                error = { msg-> fallo(msg) }
            )
        }.addOnFailureListener {
            // Si no se puede obtener la avería anterior, actualizar de todas formas
            setValue("$NODE/${averiaEditada.id}", averiaEditada,
                ok = { exito() },
                error = { msg-> fallo(msg) }
            )
        }
    }

    /**
     * Envía una notificación al cliente cuando cambia el estado de su avería
     */
    private fun enviarNotificacionCambioEstado(
        averia: Averia,
        estadoAnterior: String,
        estadoNuevo: String
    ) {
        try {
            val notificationRepo = NotificationRepository()

            // Enviar notificación al cliente
            notificationRepo.notificarCambioEstado(
                clienteId = averia.userId,
                equipoNombre = averia.equipoNombre,
                nuevoEstado = estadoNuevo,
                averiaId = averia.id
            )

            Log.d("RepairRepository", "Notificación enviada: ${averia.equipoNombre} ahora está en $estadoNuevo")
        } catch (e: Exception) {
            Log.e("RepairRepository", "Error al enviar notificación: ${e.message}")
        }
    }

    //Eliminar avería
    fun eliminarAveria(
        averiaId:String,
        exito:()->Unit,
        fallo:(String)->Unit
    ){
        if(averiaId.isBlank()){
            fallo("La avería no tiene id")
            return
        }

        delete("$NODE/$averiaId",
            ok = { exito() },
            error = { msg-> fallo(msg) }
        )
    }
}
