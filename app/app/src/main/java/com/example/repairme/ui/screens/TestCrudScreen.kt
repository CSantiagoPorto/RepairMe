package com.example.repairme.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Equipo
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.RepairRepository
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.repairme.data.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestCrudScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val deviceRepo = remember { DeviceRepository() }
    val repairRepo = remember { RepairRepository() }
    val userRepo = remember { UserRepository() }

    var devicesIdToDelete by remember { mutableStateOf("") }
    var equipoIdForAveria by remember { mutableStateOf("") }
    var averiaIdToDelete by remember { mutableStateOf("") }

    var equiposLog by remember { mutableStateOf("") }
    var averiasLog by remember { mutableStateOf("") }

    var tecnicoId by remember { mutableStateOf("") }
    var tecnicoNombre by remember { mutableStateOf("") }
    var tecnicoApellidos by remember { mutableStateOf("") }
    var tecnicoTelefono by remember { mutableStateOf("") }
    var tecnicosLog by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("TEST CRUD") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Equipos (devices)", style = MaterialTheme.typography.titleMedium)

            Button(onClick = {
                val equipo = Equipo(
                    deviceBrand = "HP",
                    deviceModel = "Pavilion",
                    deviceSN = "SN-${System.currentTimeMillis()}"
                )
                deviceRepo.crearEquipo(
                    equipo = equipo,
                    error = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    exito = { id ->
                        Toast.makeText(context, "Equipo creado: $id", Toast.LENGTH_SHORT).show()
                        Log.d("TESTCRUD", "Equipo creado id=$id")
                    }
                )
            }) { Text("Crear equipo dummy") }

            Button(onClick = {
                deviceRepo.obtenerEquipos(
                    error = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    exito = { lista ->
                        equiposLog = "Equipos: ${lista.size}\n" +
                                lista.joinToString("\n") { "${it.devicesId} - ${it.deviceBrand} ${it.deviceModel}" }
                    }
                )
            }) { Text("Listar mis equipos") }

            OutlinedTextField(
                value = devicesIdToDelete,
                onValueChange = { devicesIdToDelete = it },
                label = { Text("devicesId a eliminar") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                deviceRepo.eliminarEquipo(
                    devicesId = devicesIdToDelete.trim(),
                    error = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    exito = { Toast.makeText(context, "Equipo eliminado", Toast.LENGTH_SHORT).show() }
                )
            }) { Text("Eliminar equipo por id") }

            if (equiposLog.isNotBlank()) Text(equiposLog)

            Divider()

            Text("Averías (repairs)", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = equipoIdForAveria,
                onValueChange = { equipoIdForAveria = it },
                label = { Text("equipoId para abrir avería") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                val averia = Averia(
                    equipoId = equipoIdForAveria.trim(),
                    equipoNombre = "Equipo test",
                    tituloAveria = "No enciende",
                    descripcion = "Descripción test",
                    estado = "Pendiente"
                )

                repairRepo.abrirAveria(
                    averia = averia,
                    exito = {
                        Toast.makeText(context, "Avería creada (mira Firebase)", Toast.LENGTH_SHORT).show() },
                    fallo = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                )
            }) { Text("Abrir avería dummy") }

            Button(onClick = {
                repairRepo.obtenerAveriaUser(
                    fallo = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    exito = { lista ->
                        Log.d("TESTCRUD", "exito llamado, lista=${lista.size}")

                        averiasLog = "Averías: ${lista.size}\n" +
                                lista.joinToString("\n") { "${it.id} - ${it.estado} - ${it.tituloAveria}"

                                }
                    }
                )
            }) { Text("Listar mis averías") }

            OutlinedTextField(
                value = averiaIdToDelete,
                onValueChange = { averiaIdToDelete = it },
                label = { Text("averiaId a eliminar") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                repairRepo.eliminarAveria(
                    averiaId = averiaIdToDelete.trim(),
                    exito = { Toast.makeText(context, "Avería eliminada", Toast.LENGTH_SHORT).show() },
                    fallo = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                )
            }) { Text("Eliminar avería por id") }

            if (averiasLog.isNotBlank()) Text(averiasLog)

            Divider()

            Text("Técnicos (users)", style = MaterialTheme.typography.titleMedium)

            Button(onClick = {
                userRepo.obtenerTecnicos(
                    fallo = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    exito = { lista ->
                        tecnicosLog = "Técnicos: ${lista.size}\n" +
                                lista.joinToString("\n") {
                                    "${it.id} - ${it.name} ${it.apellidos} - ${it.phone}"
                                }
                    }
                )
            }) { Text("Listar técnicos") }

            OutlinedTextField(
                value = tecnicoId,
                onValueChange = { tecnicoId = it },
                label = { Text("id del técnico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tecnicoNombre,
                onValueChange = { tecnicoNombre = it },
                label = { Text("Nombre del técnico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tecnicoApellidos,
                onValueChange = { tecnicoApellidos = it },
                label = { Text("Apellidos del técnico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tecnicoTelefono,
                onValueChange = { tecnicoTelefono = it },
                label = { Text("Teléfono del técnico") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                val updates = mapOf(
                    "name" to tecnicoNombre.trim(),
                    "apellidos" to tecnicoApellidos.trim(),
                    "phone" to tecnicoTelefono.trim(),
                    "role" to "tecnico"
                )

                userRepo.editarTecnicoParcial(
                    tecnicoId = tecnicoId.trim(),
                    updates = updates,
                    fallo = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    exito = { Toast.makeText(context, "Técnico actualizado", Toast.LENGTH_SHORT).show() }
                )
            }) { Text("Editar técnico por id") }

            Button(onClick = {
                userRepo.eliminarTecnico(
                    tecnicoId = tecnicoId.trim(),
                    fallo = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                    exito = { Toast.makeText(context, "Técnico eliminado", Toast.LENGTH_SHORT).show() }
                )
            }) { Text("Eliminar técnico por id") }

            if (tecnicosLog.isNotBlank()) Text(tecnicosLog)
        }
    }
}