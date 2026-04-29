package com.example.repairme.ui.screens.common

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.AveriaUpdate
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.theme.naranjaLetras
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAveriaComunScreen(
    averiaId: String,
    puedeEscribirUpdate: Boolean,
    autorRol: String,
    autorNombre: String,
    onVolver: () -> Unit
) {
    val repo = remember { RepairRepository() }
    val userRepo = remember { UserRepository() }
    var averia by remember { mutableStateOf<Averia?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var textoUpdate by remember { mutableStateOf("") }
    var nombreCliente by remember { mutableStateOf("Cargando cliente...") }
    var guardandoUpdate by remember { mutableStateOf(false) }

    //carga la avería por id, también recupera el nombre del cliente
    fun cargarAveria() {
        repo.obtenerAveriaId(
            averiaId = averiaId,
            fallo = { mensaje ->
                error = mensaje
                cargando = false
            },
            exito = { resultado ->
                averia = resultado

                if (resultado.userId.isNotBlank()) {
                    userRepo.obtenerCualquierUsuarioPorId(
                        id = resultado.userId,
                        exito = { usuario ->
                            nombreCliente = usuario.name
                            cargando = false
                        },
                        fallo = {
                            nombreCliente = "Desconocido"
                            cargando = false
                        }
                    )
                } else {
                    nombreCliente = "Desconocido"
                    cargando = false
                }
            }
        )
    }

    fun formatearFecha(timestamp: Long): String {
        if (timestamp == 0L) return "Sin fecha"
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formato.format(Date(timestamp))
    }

    //devuelve el nombre del autor en función del rol
    fun obtenerNombreFallbackPorRol(): String {
        return when (autorRol.lowercase()) {
            "admin" -> "Admin"
            "tecnico" -> "Técnico"
            else -> "Usuario"
        }
    }

    //guardar autor, si es técnico pintar el nombre
    fun guardarUpdateConNombreReal(averiaActual: Averia) {
        val uidActual = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        Log.d("DetalleAveriaComun", "UID auth actual: $uidActual")
        Log.d("DetalleAveriaComun", "Rol recibido: $autorRol")
        Log.d("DetalleAveriaComun", "Nombre recibido por navegación: $autorNombre")

        if (uidActual.isBlank()) {
            val nuevaUpdate = AveriaUpdate(
                mensaje = textoUpdate.trim(),
                createdAt = System.currentTimeMillis(),
                autorId = "",
                autorNombre = if (autorNombre.isNotBlank()) autorNombre else obtenerNombreFallbackPorRol(),
                autorRol = autorRol
            )

            val averiaEditada = averiaActual.copy(
                updates = averiaActual.updates + nuevaUpdate
            )

            repo.editarAveria(
                averiaEditada = averiaEditada,
                exito = {
                    textoUpdate = ""
                    guardandoUpdate = false
                    cargarAveria()
                },
                fallo = { mensaje ->
                    guardandoUpdate = false
                    Log.d("DetalleAveriaComun", "Error al guardar update sin uid: $mensaje")
                }
            )
            return
        }

        userRepo.obtenerCualquierUsuarioPorId(
            id = uidActual,
            exito = { usuario ->
                Log.d("DetalleAveriaComun", "Usuario actual recuperado: ${usuario.name}")

                val nombreReal = if (usuario.name.isNotBlank()) {
                    usuario.name
                } else if (autorNombre.isNotBlank()) {
                    autorNombre
                } else {
                    obtenerNombreFallbackPorRol()
                }

                val nuevaUpdate = AveriaUpdate(
                    mensaje = textoUpdate.trim(),
                    createdAt = System.currentTimeMillis(),
                    autorId = uidActual,
                    autorNombre = nombreReal,
                    autorRol = autorRol
                )

                val averiaEditada = averiaActual.copy(
                    updates = averiaActual.updates + nuevaUpdate
                )

                repo.editarAveria(
                    averiaEditada = averiaEditada,
                    exito = {
                        textoUpdate = ""
                        guardandoUpdate = false
                        cargarAveria()
                    },
                    fallo = { mensaje ->
                        guardandoUpdate = false
                        Log.d("DetalleAveriaComun", "Error al guardar update con nombre real: $mensaje")
                    }
                )
            },
            fallo = { mensaje ->
                Log.d("DetalleAveriaComun", "No se pudo obtener usuario actual: $mensaje")

                val nuevaUpdate = AveriaUpdate(
                    mensaje = textoUpdate.trim(),
                    createdAt = System.currentTimeMillis(),
                    autorId = uidActual,
                    autorNombre = if (autorNombre.isNotBlank()) autorNombre else obtenerNombreFallbackPorRol(),
                    autorRol = autorRol
                )

                val averiaEditada = averiaActual.copy(
                    updates = averiaActual.updates + nuevaUpdate
                )

                repo.editarAveria(
                    averiaEditada = averiaEditada,
                    exito = {
                        textoUpdate = ""
                        guardandoUpdate = false
                        cargarAveria()
                    },
                    fallo = { mensajeGuardar ->
                        guardandoUpdate = false
                        Log.d("DetalleAveriaComun", "Error al guardar update en fallback: $mensajeGuardar")
                    }
                )
            }
        )
    }

    LaunchedEffect(Unit) {
        cargarAveria()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de reparación",
                        color = naranjaLetras,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                cargando -> CircularProgressIndicator()

                error != null -> Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )

                averiaId.isEmpty() -> Text("No existe la avería")

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = averia?.equipoNombre ?: "Desconocido",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = averia?.tituloAveria ?: "Sin nombre")
                            Text(text = averia?.descripcion ?: "No hay descripción")
                            Text(text = "Estado: ${averia?.estado ?: "Sin estado"}")
                            Text(text = "Cliente: $nombreCliente")
                        }

                        item {
                            Text(
                                text = "Actualizaciones de la reparación",
                                fontWeight = FontWeight.Bold,
                                color = naranjaLetras
                            )
                        }

                        if (puedeEscribirUpdate) {
                            item {
                                TextField(
                                    value = textoUpdate,
                                    onValueChange = { textoUpdate = it },
                                    label = { Text("Escribe una actualización") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        enabled = !guardandoUpdate,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = naranjaLetras,
                                            contentColor = Color.White
                                        ),
                                        onClick = {
                                            averia?.let { averiaActual ->
                                                if (textoUpdate.isNotBlank()) {
                                                    guardandoUpdate = true
                                                    guardarUpdateConNombreReal(averiaActual)
                                                }
                                            }
                                        }
                                    ) {
                                        Text(
                                            if (guardandoUpdate) "Guardando..." else "Añadir actualización"
                                        )
                                    }
                                }
                            }
                        }

                        if (averia?.updates?.isEmpty() != false) {
                            item {
                                Text("Todavía no hay actualizaciones")
                            }
                        } else {
                            items(averia?.updates?.sortedByDescending { it.createdAt } ?: emptyList()) { update ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = update.mensaje,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = update.autorNombre,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = formatearFecha(update.createdAt),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
