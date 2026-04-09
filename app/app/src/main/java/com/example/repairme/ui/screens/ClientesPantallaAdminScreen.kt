package com.example.repairme.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.Usuario
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesPantallaAdminScreen(
    onAddAveria: () -> Unit = {},
    onVolver: () -> Unit = {},
    onVerAveria: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onVerPresupuestos: () -> Unit = {},
    onLogOut: () -> Unit = {}

) {
    val repo = remember { UserRepository() }
    var listaClientes by remember { mutableStateOf(listOf<Usuario>()) }
    var busqueda by remember { mutableStateOf("") }
    var tipoBusqueda by remember { mutableStateOf("Nombre") }
    var expandido by remember { mutableStateOf(false) }

    var expandedClienteId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repo.obtenerUsuariosTodos(
            fallo = { },
            exito = { clientes -> listaClientes = clientes }
        )
    }

    val clientesFiltrados = listaClientes.filter { cliente ->
        if (busqueda.isBlank()) {
            true
        } else {
            when (tipoBusqueda) {
                "Nombre" -> cliente.name.contains(busqueda, ignoreCase = true) ||
                        cliente.apellidos.contains(busqueda, ignoreCase = true)

                "Email" -> cliente.email.contains(busqueda, ignoreCase = true)

                "DNI" -> cliente.dni.contains(busqueda, ignoreCase = true)

                "Localidad" -> cliente.localidad.contains(busqueda, ignoreCase = true)

                "Código Postal" -> cliente.codigoPostal.contains(busqueda)

                else -> true
            }
        }
    }

    BaseScreen(
        title = "Clientes",
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,
        onNotificationsClick = onIrNotificaciones,
        bottomNavItems = listOf(
            NavItem("Reparar", Icons.Filled.Build, onVolver),
            NavItem("Técnicos", Icons.Filled.Engineering, onVerTecnicos),
            NavItem("Clientes", Icons.Filled.Person, onVerClientes),
            NavItem("Presup.", Icons.Filled.RequestQuote, onVerPresupuestos)
        )

    ) { modifier ->
        Column(modifier = modifier.padding(16.dp)) {

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tipoBusqueda,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Buscar por") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expandido = true }
                )

                DropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    listOf("Nombre", "Email", "DNI", "Localidad", "Código Postal").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                tipoBusqueda = opcion
                                expandido = false
                                busqueda = ""
                            }
                        )
                    }
                }
            }

            TextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por $tipoBusqueda") },
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = { busqueda = "" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpiar filtro")
            }

            LazyColumn {

                items(clientesFiltrados) { cliente ->
                    var reparaciones by remember { mutableStateOf(listOf<Averia>()) }
                    //Necesito que se me abran las reparaciones del cliente al pinchar

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
                            if (expandedClienteId == cliente.id) {
                                expandedClienteId = null
                            } else {
                                expandedClienteId = cliente.id
                            }

                        }//Si está abierta y se pulsa otra vez se pone a null y se cierra
                        //Si está cerrada, la abre y guarda el id del cliente

                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${cliente.name} ${cliente.apellidos}", fontWeight = FontWeight.Bold)
                            Text(cliente.email)
                            if (expandedClienteId == cliente.id) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                //Cambios de color. Los colores los cambiamos luego a otros más bonitos
                                //Preguntar a Alex por gama de colores

                                if (reparaciones.isNotEmpty()) {
                                    reparaciones.forEach { averia ->
                                        val colorEstado = when (averia.estado) {
                                            EstadoAveria.Pendiente.name -> ColorEstadoPendiente
                                            EstadoAveria.Asignada.name -> ColorEstadoAsignada
                                            EstadoAveria.Presupuestada.name -> ColorEstadoPresupuestada
                                            EstadoAveria.EnReparacion.name -> ColorEstadoEnReparacion
                                            EstadoAveria.ListaParaRecoger.name -> ColorEstadoListaParaRecoger
                                            EstadoAveria.Declinada.name -> ColorEstadoDeclinada
                                            else -> GrisFondoPantalla
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            color = colorEstado,
                                            shape = RoundedCornerShape(9.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(9.dp)
                                            ) {
                                                Text(averia.tituloAveria, fontWeight = FontWeight.Bold)
                                                Text(averia.estado, color = naranjaLetras)
                                            }

                                        }
                                    }

                                } else {
                                    Text("No hay reparaciones que mostrar")
                                }

                            }

                        }
                    }
                }

            }
        }
    }
}