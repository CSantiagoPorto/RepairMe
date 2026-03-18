package com.example.repairme.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.UserRepository

@Composable
fun ClientesPantallaAdminScreen(
    onVolver: () -> Unit = {}
) {
    val repo = remember { UserRepository() }
    var listaClientes by remember { mutableStateOf(listOf<Usuario>()) }
    var busqueda by remember { mutableStateOf("") }
    var expandedClienteId by remember { mutableStateOf<String?>(null) }



    LaunchedEffect(Unit) {
        repo.obtenerUsuariosTodos(
            fallo = { },
            exito = { clientes -> listaClientes = clientes }
        )
    }

    val clientesFiltrados = listaClientes.filter {
        it.name.contains(busqueda, ignoreCase = true) ||
                it.apellidos.contains(busqueda, ignoreCase = true)||
                it.dni.equals(busqueda)
    }

    Scaffold(
        topBar = { /* TopAppBar con título y botón volver */ }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            TextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por nombre o apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn {

                items(clientesFiltrados) { cliente ->
                    var reparaciones by remember { mutableStateOf(listOf<Averia>()) }

                    LaunchedEffect(expandedClienteId) {
                        if (expandedClienteId == cliente.id) {
                            repo.obtenerAveriasPorUsuario(
                                userId = cliente.id,
                                fallo = {},
                                exito = { reparaciones = it }
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = {
                            expandedClienteId = if (expandedClienteId == cliente.id) null else cliente.id
                        }

                    ) {
                       Column(modifier = Modifier.padding(16.dp)) {
                           Text("${cliente.name} ${cliente.apellidos}", fontWeight = FontWeight.Bold)
                           Text(cliente.email)
                           if (expandedClienteId == cliente.id){
                               reparaciones.forEach { averia ->
                                   Text("${averia.tituloAveria}----- ${averia.estado}")


                               }
                               }

                       }
                    }
                }

            }
        }
    }
}
