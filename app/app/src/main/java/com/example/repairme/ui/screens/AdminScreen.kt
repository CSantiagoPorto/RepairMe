package com.example.repairme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.ui.componentes.AppBottomBar
import com.example.repairme.ui.componentes.NavigationItem
import com.example.repairme.ui.theme.Naranja

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onVerAverias: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onVerPresupuestos: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    // Definimos el color naranja del tema
    val naranjaPrincipal = Naranja

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel del administrador", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogOut) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        },
        bottomBar = {
            // USO DEL MOLDE: Implementamos AppBottomBar con los ítems del administrador
            // Incluimos los iconos de cada Card y el de Perfil. 
            // El de Notificaciones ya viene incluido por el molde.
            AppBottomBar(
                items = listOf(
                    NavigationItem(
                        label = "Reparaciones",
                        icon = Icons.Filled.Build,
                        onClick = onVerAverias
                    ),
                    NavigationItem(
                        label = "Técnicos",
                        icon = Icons.Filled.Engineering,
                        onClick = onVerTecnicos
                    ),
                    NavigationItem(
                        label = "Clientes",
                        icon = Icons.Filled.Person,
                        onClick = onVerClientes
                    ),
                    NavigationItem(
                        label = "Presupuestos",
                        icon = Icons.Filled.RequestQuote,
                        onClick = onVerPresupuestos
                    ),
                    NavigationItem(
                        label = "Perfil",
                        icon = Icons.Filled.Person, // Icono de perfil
                        onClick = onIrPerfil
                    )
                ),
                selectedIndex = -1, // No hay una pantalla seleccionada por defecto en el estado actual
                onNotificationsClick = {
                    // Añadir acción para el botón fijo de notificaciones
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Bienvenido, Administrador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Card de Reparaciones
            AdminCard(
                titulo = "Reparaciones",
                icono = Icons.Filled.Build,
                color = naranjaPrincipal,
                onClick = onVerAverias
            )

            // Card de Técnicos
            AdminCard(
                titulo = "Técnicos",
                icono = Icons.Filled.Engineering,
                color = naranjaPrincipal,
                onClick = onVerTecnicos
            )

            // Card de Clientes
            AdminCard(
                titulo = "Clientes",
                icono = Icons.Filled.Person,
                color = naranjaPrincipal,
                onClick = onVerClientes
            )

            // Card de Presupuestos
            AdminCard(
                titulo = "Presupuestos",
                icono = Icons.Filled.RequestQuote,
                color = naranjaPrincipal,
                onClick = onVerPresupuestos
            )
        }
    }
}

// Componente reutilizable para las Cards del Admin
@Composable
fun AdminCard(
    titulo: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen()
}
