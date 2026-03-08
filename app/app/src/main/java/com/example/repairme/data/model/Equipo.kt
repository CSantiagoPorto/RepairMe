package com.example.repairme.data.model

import com.google.firebase.database.Exclude

data class Equipo(
    @get:Exclude val devicesId: String = "",
    val deviceBrand: String = "",
    val deviceModel: String = "",
    val deviceSN: String = "",
    val userId: String = "",
    val averias: List<Averia> = emptyList()
)
