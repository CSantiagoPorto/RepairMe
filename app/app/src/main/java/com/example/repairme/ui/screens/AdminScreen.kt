package com.example.repairme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.Naranja

@Composable
fun AdminScreen(
    // Son Callbacks para navegar a otras pantallas
    onVerAverias: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onVerPresupuestos: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    // 1. Creamos la lista de botones para la barra inferior, pasando los 3 datos que se indican en el modelo 'NavItem'
    val itemsNavegacion = listOf(
        NavItem("Reparar", Icons.Filled.Build, onVerAverias),
        NavItem("Técnicos", Icons.Filled.Engineering, onVerTecnicos),
        NavItem("Clientes", Icons.Filled.Person, onVerClientes),
        NavItem("Presupuestos", Icons.Filled.RequestQuote, onVerPresupuestos)
    )

    // 2. Llamamos al modelo "BaseScreen" y le pasamos los datos de la barra inferior
    // Este modelo ya incluye la barra superior con el logotipo y la barra inferior
    BaseScreen(
        title = "ClearRepair",
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        bottomNavItems = itemsNavegacion
    ) { modifier ->
        
        // 3. Contenido de la pantalla (Cards)
        // 'modifier' es para que el contenido no se tape con la barra inferior
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenido, Administrador",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Card de Reparaciones
            AdminCard(
                titulo = "Ver reparaciones",
                icono = Icons.Filled.Build,
                onClick = onVerAverias
            )

            // Card de Técnicos
            AdminCard(
                titulo = "Añadir técnicos",
                icono = Icons.Filled.Engineering,
                onClick = onVerTecnicos
            )

            // Card de Clientes
            AdminCard(
                titulo = "Clientes",
                icono = Icons.Filled.Person,
                onClick = onVerClientes
            )

            // Card de Presupuestos
            AdminCard(
                titulo = "Presupuestos",
                icono = Icons.Filled.RequestQuote,
                onClick = onVerPresupuestos
            )
        }
    }
}

@Composable
fun AdminCard(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Naranja
            )
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = Naranja,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen()
}
