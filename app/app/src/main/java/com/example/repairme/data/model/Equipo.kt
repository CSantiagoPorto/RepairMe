package com.example.repairme.data.model

data class Equipo(
    val id: String = "",
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val averias: List<Averia> = emptyList()
)
