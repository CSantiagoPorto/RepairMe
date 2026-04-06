package com.example.repairme.data.model

data class Notificacion(
    val id: String = "", // ID de la notificacion
    val userId: String = "", // ID del usuario que debe recibir la notificacion
    val titulo: String = "",
    val mensaje: String = "",
    val leida: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(), // Ordenar notificacion de mas reciente a mas antigua
    val averiaId: String = "" // Relacion con a averia especifica
)
