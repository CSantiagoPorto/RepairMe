package com.example.repairme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.repairme.data.model.Averia
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.componentes.AppBottomBar
import com.example.repairme.ui.componentes.NavigationItem

@Composable
fun TecnicoScreen(
    onAddEquipo: () -> Unit = {},
    onAveriaClick: (String) -> Unit = {},
    onIrPerfil: () -> Unit = {}
) {
    val orangePrimary = Color(0xFFE67E22)
    val grayBackground = Color(0xFFF5F5F5)
    val isPreview = LocalInspectionMode.current

    var currentScreen by remember { mutableStateOf<String?>(null) }
    
    // 1. ESTADO COMPARTIDO: Elevamos la lista de averías a la pantalla principal.
    var listaAverias by remember { mutableStateOf(listOf<Averia>()) }
    val repo = remember { RepairRepository() }

    // 2. SINCRONIZACIÓN EN TIEMPO REAL:
    LaunchedEffect(Unit) {
        if (!isPreview) {
            repo.obtenerAveriasTecnico(
                fallo = {},
                exito = { averias -> listaAverias = averias }
            )
        } else {
            listaAverias = listOf(
                Averia(id = "1", equipoNombre = "Laptop Dell", estado = "En reparación", tituloAveria = "Pantalla rota"),
                Averia(id = "2", equipoNombre = "iPhone 13", estado = "Reparado", tituloAveria = "Cambio batería")
            )
        }
    }

    Scaffold(
        containerColor = grayBackground,
        bottomBar = {
            // 3. USO DEL MOLDE: Implementamos AppBottomBar con los ítems del técnico
            // Aquí definimos los botones dinámicos y el de notificaciones es fijo por el molde.
            AppBottomBar(
                items = listOf(
                    NavigationItem(
                        label = "Reparar",
                        icon = Icons.Filled.Build,
                        onClick = { currentScreen = "repair" }
                    ),
                    NavigationItem(
                        label = "Reparados",
                        icon = Icons.Filled.Computer,
                        onClick = { currentScreen = "repaired" }
                    ),
                    NavigationItem(
                        label = "Perfil",
                        icon = Icons.Filled.Person,
                        onClick = onIrPerfil
                    )
                ),
                selectedIndex = when(currentScreen) {
                    "repair" -> 0
                    "repaired" -> 1
                    else -> -1
                },
                onNotificationsClick = {
                    // Acción para el botón de notificaciones (fijo en el molde)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ClearRepair",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = orangePrimary
                )
            }

            // 4. NAVEGACIÓN Y FILTRADO:
            when (currentScreen) {
                "repair" -> RepairListScreen(
                    listaAverias = listaAverias,
                    repo = repo,
                    orangePrimary = orangePrimary,
                    onBack = { currentScreen = null },
                    onAveriaClick = onAveriaClick
                )
                "repaired" -> RepairedListScreen(
                    listaAverias = listaAverias,
                    orangePrimary = orangePrimary,
                    onBack = { currentScreen = null },
                    onAveriaClick = onAveriaClick
                )
                else -> HomeContent(orangePrimary) { screen -> currentScreen = screen }
            }
        }
    }
}

@Composable
fun HomeContent(orangePrimary: Color, onCardClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardItem(
            title = "Equipos en\nreparación",
            icon = Icons.Filled.Build,
            accentColor = orangePrimary,
            onClick = { onCardClick("repair") }
        )

        CardItem(
            title = "Equipos\nreparados",
            icon = Icons.Filled.Computer,
            accentColor = orangePrimary,
            onClick = { onCardClick("repaired") }
        )
    }
}

@Composable
fun CardItem(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun RepairListScreen(
    listaAverias: List<Averia>,
    repo: RepairRepository,
    orangePrimary: Color,
    onBack: () -> Unit,
    onAveriaClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = orangePrimary
            )
        ) {
            Text("← Volver")
        }
        Text(
            text = "Equipos en reparación",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = orangePrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        
        // FILTRADO PARA SECCIÓN "REPARAR":
        val enReparacion = listaAverias.filter { it.estado != "Reparado" }

        if (enReparacion.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay equipos en reparación", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(enReparacion) { averia ->
                    RepairItem(
                        title = "Equipo ${averia.equipoNombre}",
                        currentState = averia.estado,
                        orangePrimary = orangePrimary,
                        onStateChange = { newState ->
                            val averiaActualizada = averia.copy(estado = newState)
                            repo.editarAveria(
                                averiaEditada = averiaActualizada,
                                exito = {},
                                fallo = {}
                            )
                        },
                        onAveriaClick = { onAveriaClick(averia.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RepairedListScreen(
    listaAverias: List<Averia>,
    orangePrimary: Color,
    onBack: () -> Unit,
    onAveriaClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = orangePrimary
            )
        ) {
            Text("← Volver")
        }
        Text(
            text = "Equipos reparados",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = orangePrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // FILTRADO PARA SECCIÓN "REPARADOS":
        val reparados = listaAverias.filter { it.estado == "Reparado" }

        if (reparados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay equipos reparados todavía", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reparados) { averia ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable { onAveriaClick(averia.id) },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = averia.equipoNombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = averia.tituloAveria,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "Completado",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepairItem(
    title: String,
    currentState: String = "Esperando confirmación",
    orangePrimary: Color = Color(0xFFE67E22),
    onStateChange: (String) -> Unit = {},
    onAveriaClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val states = listOf(
        "Esperando confirmación",
        "En reparación",
        "Reparado"
    )
    val buttonColor = if (currentState == "Reparado") {
        Color(0xFF4CAF50) // Verde
    } else {
        orangePrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = { onAveriaClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    ),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = currentState,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    states.forEach { state ->
                        DropdownMenuItem(
                            text = { Text(state) },
                            onClick = {
                                onStateChange(state)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TecnicoScreenPreview() {
    MaterialTheme {
        TecnicoScreen()
    }
}
