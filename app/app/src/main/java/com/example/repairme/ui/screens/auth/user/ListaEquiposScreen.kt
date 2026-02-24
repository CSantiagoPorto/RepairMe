package com.example.repairme.ui.screens.auth.user

import androidx.compose.runtime.Composable
import  com.example.repairme.data.model.Equipo

class ListaEquiposScreen {

    @Composable
    fun ListaEquiposScreen(
        titulo:String= "Mis equipos",
        onVolver:()->Unit={},
        onPerfil:()->Unit={},
        onInicio:()->Unit={},
        onCerrarSesion:()->Unit={},
        onNuevoEquipo:()->Unit={},
        onVerEquipo:(Equipo)->Unit={}
    ){

    }


}