package com.example.repairme.ui.screens.user

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.repairme.data.model.Equipo
import com.example.repairme.data.repository.DeviceRepository


//ESTA ES UNA CLASE QUE USO COMO CAMPO DE PRUEBAS
//ESTO LUEGO SE VA A BORRAR
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ListaEquiposScreen(
    titulo: String = "Mis equipos",
    onVolver: () -> Unit = {},
    onVerEquipo: (Equipo) -> Unit = {},
    onCrearEquipoNuevo: ()->Unit={}
) {
    var listaEquipos by remember { mutableStateOf(listOf<Equipo>()) }


    LaunchedEffect(Unit) {
        val repo= DeviceRepository()
        repo.obtenerEquipos(
            error = {onVolver()},
            exito = {equipos -> listaEquipos= equipos }
        )
    }



}