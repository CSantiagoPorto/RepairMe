package com.example.repairme.ui.screens.auth.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.AdminRepository
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.ColorEstadoAsignada
import com.example.repairme.ui.theme.ColorEstadoDeclinada
import com.example.repairme.ui.theme.ColorEstadoEnReparacion
import com.example.repairme.ui.theme.ColorEstadoListaParaRecoger
import com.example.repairme.ui.theme.ColorEstadoPendiente
import com.example.repairme.ui.theme.ColorEstadoPresupuestada
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.naranjaLetras


@Composable
fun NuevaAveriaAdmin(
    onVolver: () -> Unit = {},
    onVerAverias: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onVerPresupuestos:()->Unit={},
    onLogOut: () -> Unit = {}
) {
    val repoReparaciones = remember { RepairRepository() }
    val repoDispositivos = remember { DeviceRepository() }
    val repoAdmin= remember { AdminRepository() }
    val repoUsuario=remember { UserRepository() }


    //var todasAverias by remember { mutableStateOf(listOf<Averia>()) }
    var busqueda by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf(Usuario()) }
    var listaClientes by remember { mutableStateOf(listOf<Usuario>()) }
    var marca by rememberSaveable { mutableStateOf("") }
    var modelo by rememberSaveable { mutableStateOf("") }
    var numSerie by remember { mutableStateOf("") }


    var añadirAveria by rememberSaveable { mutableStateOf(false) }
    var tituloAveria by rememberSaveable { mutableStateOf("") }
    var descripcionAveria by rememberSaveable { mutableStateOf("") }
    var reparaciones by remember { mutableStateOf(listOf<Averia>()) }

    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarNuevoFormulario by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repoUsuario.obtenerUsuariosTodos(
            fallo = { },
            exito = { clientes -> listaClientes = clientes }
        )
    }

    val clientesFiltrados = listaClientes.filter {
        it.name.contains(busqueda, ignoreCase = true) ||
                it.apellidos.contains(busqueda, ignoreCase = true) ||
                it.dni.equals(busqueda, ignoreCase = true)||
                it.email.equals(busqueda, ignoreCase = true)
    }

    BaseScreen(
        title = "Crear Reparación",
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,

        notificationBadgeCount = 0,
        bottomNavItems = listOf(
            NavItem("Reparar", Icons.Filled.Build, onVolver),
            NavItem("Técnicos", Icons.Filled.Engineering, onVerTecnicos),
            NavItem("Clientes", Icons.Filled.Person, onVerClientes),
            NavItem("Presup.", Icons.Filled.RequestQuote, onVerPresupuestos)
        )

    ) { modifier ->
        Column(modifier = modifier.padding(16.dp)) {
            if(clienteSeleccionado.id.isEmpty()){
                //Si no hay un cliente filtrado, muestro el buscador

            TextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por nombre, apellidos, dni o email") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn {

                items(clientesFiltrados) {cliente->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = {clienteSeleccionado=cliente}
                    ) {

                            Text("${cliente.name} ${cliente.apellidos}", fontWeight = FontWeight.Bold)
                            Text(cliente.email)

                    }

                }

            }
        }else{
        Text("Cliente: ${clienteSeleccionado.name}")
    }
        }
    }



}