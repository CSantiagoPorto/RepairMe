package com.example.repairme.data.model

data class Tecnico(
    val tecnicoId: String = "",
    val nombre: String = "",
    val email: String = "",
    val fechaAlta: Long = 0L,
    val estado: String = "Activo",
    val role: String= "TECNICO"
)
