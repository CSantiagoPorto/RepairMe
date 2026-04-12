package com.example.repairme.data.repository

import android.util.Log
import com.example.repairme.data.model.Notificacion
import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class NotificationRepository : OperationsTemplateRepository() {
    private val NODE = "notifications"
    private val USERS_NODE = "users"
    private val auth by lazy { FirebaseAuth.getInstance() }

    // Funcion para guardar una notificacion. Recibe el objeto y 2 callbacks: exito y fallo
    fun enviarNotificacion(
        notificacion: Notificacion,
        exito: () -> Unit = {},
        fallo: (String) -> Unit = {}
    ) {
        // Genera un ID unico y aleatorio dentro del nodo "notifications"
        val notificacionUid = newId(NODE)
        // Crea una copia de la notificacion con el ID generado
        val finalNotif = notificacion.copy(id = notificacionUid)

        // Escribir en la base de datos
        setValue("$NODE/$notificacionUid", finalNotif,
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }

    /**
     * Escucha las notificaciones no leídas del usuario actual en tiempo real.
     */
    fun escucharNotificacionesNoLeidas(
        onUpdate: (Int) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        ref(NODE).orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.children.count {
                        val notif = it.getValue(Notificacion::class.java)
                        notif != null && !notif.leida
                    }
                    onUpdate(count)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /**
     * Obtiene todas las notificaciones del usuario actual.
     */
    fun obtenerMisNotificaciones(
        onUpdate: (List<Notificacion>) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        ref(NODE).orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lista = snapshot.children.mapNotNull { it.getValue(Notificacion::class.java) }
                        .sortedByDescending { it.createdAt }
                    onUpdate(lista)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     */
    fun marcarComoLeidas() {
        val userId = auth.currentUser?.uid ?: return
        ref(NODE).orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updates = mutableMapOf<String, Any?>()
                    for (child in snapshot.children) {
                        val notif = child.getValue(Notificacion::class.java)
                        if (notif != null && !notif.leida) {
                            updates["${child.key}/leida"] = true
                        }
                    }
                    if (updates.isNotEmpty()) {
                        updateChildren(NODE, updates, {}, {})
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /**
     * Marca una notificación específica como leída
     */
    fun marcarNotificacionComoLeida(notificationId: String) {
        ref("$NODE/$notificationId/leida").setValue(true)
    }

    /**
     * Notifica al cliente y a todos los administradores sobre un cambio de estado en una avería.
     */
    fun notificarCambioEstado(
        clienteId: String,
        equipoNombre: String,
        nuevoEstado: String,
        averiaId: String
    ) {
        // 1. Notificación para el cliente
        val notifCliente = Notificacion(
            userId = clienteId,
            titulo = "Estado de reparación actualizado",
            mensaje = "Tu equipo '$equipoNombre' ahora está en estado: $nuevoEstado.",
            averiaId = averiaId
        )
        enviarNotificacion(notifCliente)

        // 2. Buscar administradores y notificarles
        ref(USERS_NODE).orderByChild("role").equalTo("admin")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val adminId = child.key ?: ""
                        if (adminId.isNotEmpty()) {
                            val notifAdmin = Notificacion(
                                userId = adminId,
                                titulo = "Cambio de estado por técnico",
                                mensaje = "La avería de $equipoNombre (ID: $averiaId) ha cambiado a: $nuevoEstado.",
                                averiaId = averiaId
                            )
                            enviarNotificacion(notifAdmin)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /**
     * Notifica a un técnico que se le ha asignado una avería.
     */
    fun notificarAsignacionTecnico(
        tecnicoId: String,
        equipoNombre: String,
        averiaId: String
    ) {
        val notif = Notificacion(
            userId = tecnicoId,
            titulo = "Nueva avería asignada",
            mensaje = "Se te ha asignado la reparación del equipo: $equipoNombre.",
            averiaId = averiaId
        )
        enviarNotificacion(notif)
    }

    /**
     * Notifica al admin que un usuario ha aprobado un presupuesto.
     */
    fun notificarPresupuestoAprobado(
        equipoNombre: String,
        nombreUsuario: String,
        averiaId: String
    ) {
        Log.d("NotificationRepo", "notificarPresupuestoAprobado llamado: usuario=$nombreUsuario, equipo=$equipoNombre, averiaId=$averiaId")

        // Buscar todos los administradores y notificarles
        ref(USERS_NODE).get().addOnSuccessListener { snapshot ->
            var adminEncontrado = false
            for (child in snapshot.children) {
                val usuario = child.getValue(Usuario::class.java)
                Log.d("NotificationRepo", "Usuario encontrado: role=${usuario?.role}, id=${child.key}")

                if (usuario != null && usuario.role.lowercase() == "admin") {
                    adminEncontrado = true
                    val adminId = child.key ?: ""
                    if (adminId.isNotEmpty()) {
                        val notif = Notificacion(
                            userId = adminId,
                            titulo = "Presupuesto aprobado",
                            mensaje = "El usuario $nombreUsuario ha aprobado el presupuesto para el equipo: $equipoNombre.",
                            averiaId = averiaId
                        )
                        Log.d("NotificationRepo", "Enviando notificación a admin: $adminId")
                        enviarNotificacion(notif)
                    }
                }
            }
            Log.d("NotificationRepo", "Admin encontrado: $adminEncontrado, Total usuarios: ${snapshot.childrenCount}")
        }.addOnFailureListener { e ->
            Log.e("NotificationRepo", "Error al buscar admins: ${e.message}")
        }
    }
}

