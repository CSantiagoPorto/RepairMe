package com.example.repairme.data.model


data class Averia(
    val id: String = "",
    val userId: String = "",
    val tecnicoId: String = "",
    val equipoId: String = "",
    val tituloAveria: String="",
    val equipoNombre:String="",
    val descripcion: String = "",
    val estado: String = "Pendiente",
    val precioAproximado: Double = 0.0,
    val presupuestoDefinitivo: Double = 0.0,
    val presupuestoAceptado: Boolean? = null,
    val createdAt: Long = 0L,
    val updates: List<AveriaUpdate> = emptyList()
)
