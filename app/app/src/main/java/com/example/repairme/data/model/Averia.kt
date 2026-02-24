package com.example.repairme.data.model

data class Averia(
    val id: String = "",
    val userId:String="",
    val descripcion: String = "",
    val estado: String = "Abierta",
    val createdAt: Long = 0L,
    val updates: List<AveriaUpdate> = emptyList()
)
