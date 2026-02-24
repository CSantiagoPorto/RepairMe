package com.example.repairme.data.model

import com.google.firebase.database.Exclude

data class Usuario (
   @get:Exclude val id: String = "",//Necesito excluirlo como documento en la bbdd pero lo necesito luego para poder filtrar por id y recuperar luego una luista de users
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "user",
    val createdAt: Long = 0L,
    val apellidos: String = "",
    val direccion: String = "",
    val codigoPostal: String = "",
    val localidad: String = "",
    val dni: String = ""
){




}