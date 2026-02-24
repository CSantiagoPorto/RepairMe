package com.example.repairme.data.model

data class Equipo(
    val devicesId: String = "",
    val deviceBrand: String = "",
    val deviceModel: String = "",
    val deviceSN: String = "",
    val userId: String = "",
    val averias: List<Averia> = emptyList()
)
