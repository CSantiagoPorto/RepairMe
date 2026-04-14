package com.example.repairme.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.EstadoTecnico
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.TecnicoRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.Naranja

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTecnicosScreen(
    onVolver: () -> Unit = {},
    onRegistrarTecnico: () -> Unit = {},
    onVerAverias: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onIrHome: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onVerPresupuestos: () -> Unit = {},
    onLogOut: () -> Unit = {},
    notificacionesNoLeidas: Int = 0
) {
    var listaTecnicos by remember { mutableStateOf(listOf<Usuario>()) } // lista de técnicos de firebase
    var cargando by remember { mutableStateOf(true) } // para saber si cargaron los datos
    var error by remember { mutableStateOf<String?>(null) }
    var busqueda by remember { mutableStateOf("") } // guarda lo que se escribe en el buscador
    var filtroEstado by remember { mutableStateOf("Todos") }

    val repo = remember { TecnicoRepository() }

    fun cargarTecnicos() {
        cargando = true
        repo.obtenerTecnicos(
            fallo = { mensaje ->
                error = mensaje
                cargando = false
                Log.d("ListaTecnicosScreen", "Error cargando técnicos: $mensaje")
            },
            exito = { tecnicos ->
                listaTecnicos = tecnicos
                cargando = false
                Log.d("ListaTecnicosScreen", "Técnicos cargados: ${tecnicos.size}")
            }
        )
    }

    LaunchedEffect(Unit) {
        cargarTecnicos() // ejecuta siempre que se abra la pantalla
    }

    // preparamos lista a mostrar
    val listaFiltrada = listaTecnicos.filter { tecnico ->
        val estadoTecnico = tecnico.estado.ifBlank { EstadoTecnico.Activo.name } // en caso de estar vacío lo consideramos Activo

        val coincideBusqueda =
            tecnico.name.contains(busqueda, ignoreCase = true) ||
                    tecnico.email.contains(busqueda, ignoreCase = true)

        val coincideEstado = when (filtroEstado) {
            "Todos" -> true
            else -> estadoTecnico == filtroEstado
        }

        coincideBusqueda && coincideEstado
    }

    BaseScreen(
        title = "Listado de técnicos",
        onIrHome = onIrHome,
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,
        onNotificationsClick = onIrNotificaciones,
        notificationBadgeCount = notificacionesNoLeidas,
    ) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // botón para registrar técnico, ocupa todo el ancho
            Button(
                onClick = onRegistrarTecnico,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Naranja
                )
            ) {
                Text("Registrar técnico")
            }
            // input del texto a escribir en el buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar por nombre o email") }
            )

            // filtros sobre el estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = { filtroEstado = "Todos" }) {
                    Text(
                        text = "Todos",
                        color = if (filtroEstado == "Todos") Naranja else MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(onClick = { filtroEstado = EstadoTecnico.Activo.name }) {
                    Text(
                        text = "Activos",
                        color = if (filtroEstado == EstadoTecnico.Activo.name) Naranja else MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(onClick = { filtroEstado = EstadoTecnico.Inactivo.name }) {
                    Text(
                        text = "Inactivos",
                        color = if (filtroEstado == EstadoTecnico.Inactivo.name) Naranja else MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(onClick = { filtroEstado = EstadoTecnico.Vacaciones.name }) {
                    Text(
                        text = "Vacaciones",
                        color = if (filtroEstado == EstadoTecnico.Vacaciones.name) Naranja else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // filtrado sobre el estado
            when {
                cargando -> CircularProgressIndicator()

                error != null -> Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )

                listaFiltrada.isEmpty() -> Text("No hay técnicos que coincidan con el filtro")

                else -> {
                    // muestra la lista de técnicos ya filtrados
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // se recorre cada técnico filtrado y creamos una card individual
                        items(listaFiltrada) { tecnico ->
                            val estadoMostrado = tecnico.estado.ifBlank { EstadoTecnico.Activo.name } // si no tiene estado, lo consideramos Activo
                            var expandido by remember { mutableStateOf(false) } // para el menú desplegable "Cambiar estado"

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // dividimos cada card en dos para dejar el botón a la derecha
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // columna izquierda
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = tecnico.name.ifBlank { "Sin nombre" },
                                            fontWeight = FontWeight.Bold,
                                            color = Naranja
                                        )

                                        if (tecnico.email.isNotBlank()) {
                                            Text(text = tecnico.email)
                                        }

                                        Text(
                                            text = "Estado: $estadoMostrado"
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // columna derecha
                                    Column {
                                        Button(
                                            onClick = { expandido = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Naranja
                                            )
                                        ) {
                                            Text("Cambiar estado")
                                        }

                                        DropdownMenu(
                                            expanded = expandido,
                                            onDismissRequest = { expandido = false }
                                        ) {
                                            // menú desplegable "Cambiar estado" - Activo, Inactivo, Vacaciones
                                            DropdownMenuItem(
                                                text = { Text(EstadoTecnico.Activo.name) },
                                                onClick = {
                                                    expandido = false
                                                    repo.cambiarEstadoTecnico(
                                                        tecnicoId = tecnico.id,
                                                        nuevoEstado = EstadoTecnico.Activo.name,
                                                        fallo = { mensaje ->
                                                            error = mensaje
                                                        },
                                                        exito = {
                                                            cargarTecnicos()
                                                        }
                                                    )
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text(EstadoTecnico.Inactivo.name) },
                                                onClick = {
                                                    expandido = false
                                                    repo.cambiarEstadoTecnico(
                                                        tecnicoId = tecnico.id,
                                                        nuevoEstado = EstadoTecnico.Inactivo.name,
                                                        fallo = { mensaje ->
                                                            error = mensaje
                                                        },
                                                        exito = {
                                                            cargarTecnicos()
                                                        }
                                                    )
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text(EstadoTecnico.Vacaciones.name) },
                                                onClick = {
                                                    expandido = false
                                                    repo.cambiarEstadoTecnico(
                                                        tecnicoId = tecnico.id,
                                                        nuevoEstado = EstadoTecnico.Vacaciones.name,
                                                        fallo = { mensaje ->
                                                            error = mensaje
                                                        },
                                                        exito = {
                                                            cargarTecnicos()
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}