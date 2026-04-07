package com.example.repairme.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.AzulAdmin
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.botonNaranja

@Composable
fun TecnicoScreen(
    onAddEquipo: () -> Unit = {},
    onAveriaClick: (String) -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    onReparacionesFinalizadasClick: (String) -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    val orangePrimary = Naranja
    val grayBackground = Color(0xFFF5F5F5)

    // Estado para controlar qué pantalla se muestra dentro del técnico (Home, Reparar o Reparados)
    var currentScreen by remember { mutableStateOf<String?>(null) }

    // 1. Configuramos los botones de la barra inferior para que cambien el 'currentScreen' internamente
    val itemsNavegacion = listOf(
        NavItem("Reparar", Icons.Filled.Build, { currentScreen = "repair" }),
        NavItem("Reparados", Icons.Filled.Computer, { currentScreen = "repaired" })
    )

    // 2. Usamos el modelo BaseScreen que unifica la TopBar (con Logo) y la BottomBar (con Notificaciones fijas)
    BaseScreen(
        title = "ClearRepair",
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        bottomNavItems = itemsNavegacion,
        onNotificationsClick = onIrNotificaciones,
        notificationBadgeCount = 0
    ) { modifier ->
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(grayBackground)
        ) {
            // 3. Navegación interna basada en el estado 'currentScreen'
            when (currentScreen) {
                "repair" -> RepairListScreen(
                    orangePrimary = orangePrimary,
                    onBack = { currentScreen = null },
                    onAveriaClick = onAveriaClick
                )
                "repaired" -> RepairedListScreen(
                    orangePrimary = orangePrimary,
                    onBack = { currentScreen = null },
                    onAveriaClick = onReparacionesFinalizadasClick
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
        // Card 1: Equipos en reparación
        CardItem(
            title = "Equipos en\nreparación",
            icon = Icons.Filled.Build,
            accentColor = orangePrimary,
            onClick = { onCardClick("repair") }
        )

        // Card 2: Equipos reparados
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

// --- LÓGICA DE DATOS (BACKEND) RESTAURADA ---

@Composable
fun RepairListScreen(orangePrimary: Color, onBack: () -> Unit, onAveriaClick: (String) -> Unit) {
    var listaAverias by remember { mutableStateOf(listOf<Averia>()) }
    val repo = remember { RepairRepository() }
    
    LaunchedEffect(Unit) {
        repo.obtenerAveriasTecnico(
            fallo = {},
            exito = { averias -> listaAverias = averias.filter {
                it.estado != EstadoAveria.Reparado.name && it.estado != EstadoAveria.Declinada.name
            } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = orangePrimary)) {
            Text("← Volver")
        }
        Text(text = "Equipos en reparación", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = orangePrimary, modifier = Modifier.padding(vertical = 12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaAverias) { averia ->
                RepairItem(
                    title = "${averia.equipoNombre}",
                    currentState = averia.estado,
                    orangePrimary = orangePrimary,
                    onStateChange = { newState ->
                        val fechaListo= if(newState== EstadoAveria.ListaParaRecoger.name){
                            System.currentTimeMillis()
                        }else{
                            averia.fechaListo//Si no está lista para recoger se conseva la fecha a null
                        }
                        repo.editarAveria(averia.copy(
                            estado = newState,
                            fechaListo = fechaListo),
                            {}, {})
                    },
                    onAveriaClick = { onAveriaClick(averia.id) }
                )
            }
        }
    }
}

@Composable
fun RepairedListScreen(orangePrimary: Color, onBack: () -> Unit, onAveriaClick: (String) -> Unit) {
    var listaReparadas by remember { mutableStateOf(listOf<Averia>()) }
    val repo = remember { RepairRepository() }

    LaunchedEffect(Unit) {
        repo.obtenerAveriasTecnico(
            fallo = {},
            exito = { averias -> listaReparadas = averias.filter { it.estado == "Reparado" } }
        )
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = orangePrimary)) {
            Text("← Volver")
        }
        Text(text = "Equipos reparados", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = orangePrimary, modifier = Modifier.padding(vertical = 12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaReparadas) { averia ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onAveriaClick(averia.id) },
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(text = "${averia.equipoNombre} --- ${averia.tituloAveria}", modifier = Modifier.padding(16.dp), fontSize = 14.sp)
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
        EstadoAveria.EnReparacion.name,
        EstadoAveria.PendienteMaterial.name,
        EstadoAveria.ListaParaRecoger.name,
        EstadoAveria.Reparado.name
    )
    val buttonColor = if (currentState == "Reparado") Color(0xFF4CAF50) else orangePrimary

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = { onAveriaClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, fontSize = 14.sp, modifier = Modifier.weight(1f))
            //Si está en estado== asignada me va a mostrar el botón de presupuestar
            if(currentState== EstadoAveria.Asignada.name){
                Button(onClick = onAveriaClick, colors = ButtonDefaults.buttonColors(containerColor = AzulAdmin), modifier = Modifier.height(36.dp)
                ) { Text("Presupuestar", fontSize = 12.sp, color = Color.White)}
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box {
                Button(onClick = { expanded = true }, colors = ButtonDefaults.buttonColors(containerColor = buttonColor), modifier = Modifier.height(36.dp)) {
                    Text(text = currentState, fontSize = 12.sp, color = Color.White)

                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    states.forEach { state ->
                        DropdownMenuItem(text = { Text(state) }, onClick = { onStateChange(state); expanded = false })
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TecnicoScreenPreview() {
    TecnicoScreen()
}
