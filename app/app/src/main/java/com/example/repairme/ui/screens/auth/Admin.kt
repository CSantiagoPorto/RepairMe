package com.example.repairme.ui.screens.auth.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja

@Composable
fun AdminScreen(onNavigateToRegisterTecnicoScreen: () -> Unit = {}) {
    // Placeholder data
    val reparaciones = listOf(
        "Reparación 1: Pantalla rota",
        "Reparación 2: Batería defectuosa",
        "Reparación 3: Teclado no responde",
        "Reparación 4: Cargador dañado",
        "Reparación 5: Altavoz sin sonido",
        "Reparación 6: Cámara no funciona",
        "Reparación 7: WiFi intermitente",
        "Reparación 8: Botón de encendido atascado",
        "Reparación 9: Pantalla táctil insensible",
        "Reparación 10: Sobrecalentamiento",
        "Reparación 11: Memoria llena",
        "Reparación 12: Aplicaciones se cierran solas",
        "Reparación 13: Bluetooth no conecta",
        "Reparación 14: Vibración no funciona",
        "Reparación 15: GPS inexacto"
    )
    val tecnicos = listOf("Carla", "Alex", "Adri") // Esto son ejemlos, deberia rellenarse con la base de datos una vez logeamos a los tecnicos

    var selectedReparacion by remember { mutableStateOf<String?>(null) }
    var selectedTecnico by remember { mutableStateOf<String?>(null) }
    var expandedReparaciones by remember { mutableStateOf(false) }
    var expandedTecnicos by remember { mutableStateOf(false) }
    var assignedRepairs by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisFondoPantalla)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Nombre de la app arriba
            Text(
                text = "RepairMe",
                style = MaterialTheme.typography.headlineMedium,
                color = Naranja,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Desplegables de reparaciones y técnicos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Desplegable de reparaciones
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { expandedReparaciones = !expandedReparaciones },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = selectedReparacion ?: "Seleccionar reparación",
                            color = if (selectedReparacion != null) Color.Black else Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                    DropdownMenu(
                        expanded = expandedReparaciones,
                        onDismissRequest = { expandedReparaciones = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        reparaciones.filter { !assignedRepairs.containsKey(it) }.forEach { reparacion ->
                            DropdownMenuItem(
                                text = { Text(reparacion) },
                                onClick = {
                                    selectedReparacion = reparacion
                                    expandedReparaciones = false
                                }
                            )
                        }
                    }
                }

                // Desplegable de técnicos
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { expandedTecnicos = !expandedTecnicos },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = selectedTecnico ?: "Seleccionar técnico",
                            color = if (selectedTecnico != null) Color.Black else Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                    DropdownMenu(
                        expanded = expandedTecnicos,
                        onDismissRequest = { expandedTecnicos = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tecnicos.forEach { tecnico ->
                            DropdownMenuItem(
                                text = { Text(tecnico) },
                                onClick = {
                                    selectedTecnico = tecnico
                                    expandedTecnicos = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabla con información de la reparación seleccionada
            if (selectedReparacion != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Detalles de la reparación",
                            style = MaterialTheme.typography.titleMedium,
                            color = Naranja
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fila: Reparación
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reparación:",
                                modifier = Modifier.weight(0.4f),
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(Color.Gray)
                            )
                            Text(
                                text = selectedReparacion ?: "",
                                modifier = Modifier
                                    .weight(0.6f)
                                    .padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Línea divisora
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.LightGray)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fila: Técnico asignado
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Técnico:",
                                modifier = Modifier.weight(0.4f),
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(Color.Gray)
                            )
                            Text(
                                text = selectedTecnico ?: "Sin asignar",
                                modifier = Modifier
                                    .weight(0.6f)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }

                // Botón Asignar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        onClick = {
                            if (selectedTecnico != null && selectedReparacion != null) {
                                assignedRepairs = assignedRepairs.toMutableMap().apply {
                                    this[selectedReparacion!!] = selectedTecnico!!
                                }
                                selectedReparacion = null
                                selectedTecnico = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Naranja)
                    ) {
                        Text("Asignar")
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selecciona una reparación para ver los detalles",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabla de asignaciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Encabezados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color(0xFFF0F0F0)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reparación",
                            modifier = Modifier.weight(0.45f),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = androidx.compose.ui.unit.TextUnit(12f, androidx.compose.ui.unit.TextUnitType.Sp)
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color.Gray)
                        )
                        Text(
                            text = "Técnico",
                            modifier = Modifier
                                .weight(0.45f)
                                .padding(start = 8.dp),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = androidx.compose.ui.unit.TextUnit(12f, androidx.compose.ui.unit.TextUnitType.Sp)
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color.Gray)
                        )
                        Text(
                            text = "Estado",
                            modifier = Modifier
                                .weight(0.1f)
                                .padding(start = 8.dp),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = androidx.compose.ui.unit.TextUnit(12f, androidx.compose.ui.unit.TextUnitType.Sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filas de datos
                    LazyColumn {
                        items(assignedRepairs.toList()) { (reparacion, tecnico) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = reparacion,
                                    modifier = Modifier.weight(0.45f),
                                    fontSize = androidx.compose.ui.unit.TextUnit(11f, androidx.compose.ui.unit.TextUnitType.Sp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(20.dp)
                                        .background(Color.Gray)
                                )
                                Text(
                                    text = tecnico,
                                    modifier = Modifier
                                        .weight(0.45f)
                                        .padding(start = 8.dp),
                                    fontSize = androidx.compose.ui.unit.TextUnit(11f, androidx.compose.ui.unit.TextUnitType.Sp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(20.dp)
                                        .background(Color.Gray)
                                )
                                Text(
                                    text = "✓",
                                    modifier = Modifier
                                        .weight(0.1f)
                                        .padding(start = 8.dp),
                                    color = Color.Green,
                                    fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    if (assignedRepairs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay reparaciones asignadas",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Botón flotante con signo más abajo a la derecha
        FloatingActionButton(
            onClick = onNavigateToRegisterTecnicoScreen,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Naranja
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Registrar técnico", tint = Color.White)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    MaterialTheme {
        AdminScreen()
    }
}
