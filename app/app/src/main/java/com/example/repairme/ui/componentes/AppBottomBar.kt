package com.example.repairme.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.ui.theme.Naranja

// 1. Clase de datos para los botones dinámicos
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppBottomBar(
    items: List<NavigationItem>, // Los botones que cambian (Reparaciones, Técnicos, etc.)
    selectedIndex: Int,          // Cuál está seleccionado (-1 si ninguno)
    onNotificationsClick: () -> Unit // Acción para el botón de notificaciones
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // 2. Pintamos los ítems dinámicos que nos pasen (Admin, Técnico o Usuario)
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 10.sp) },
                selected = selectedIndex == index,
                onClick = item.onClick,
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Naranja,
                    unselectedTextColor = Naranja,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        // 3. BOTÓN FIJO: Siempre aparece el icono de notificaciones al final
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones") },
            label = { Text("Notificaciones", fontSize = 10.sp) },
            selected = false, // Las notificaciones suelen abrir un diálogo o pantalla aparte
            onClick = onNotificationsClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Naranja,
                unselectedTextColor = Naranja
            )
        )
    }
}