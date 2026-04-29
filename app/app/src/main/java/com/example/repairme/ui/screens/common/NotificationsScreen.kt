package com.example.repairme.ui.screens.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Notificacion
import com.example.repairme.data.repository.NotificationRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    onIrHome: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onIrServicios: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    var notificaciones by remember { mutableStateOf<List<Notificacion>>(emptyList()) }
    val repo = remember { NotificationRepository() }
    var marcarTodasComoLeidas by remember { mutableStateOf(false) }

    // Cargar notificaciones cuando se abre la pantalla
    LaunchedEffect(Unit) {
        repo.obtenerMisNotificaciones { notifs ->
            notificaciones = notifs
            Log.d("NotificationsScreen", "Notificaciones cargadas: ${notifs.size}")
        }
    }

    // Marcar como leídas si se pulsa el botón
    LaunchedEffect(marcarTodasComoLeidas) {
        if (marcarTodasComoLeidas) {
            repo.marcarComoLeidas()
            marcarTodasComoLeidas = false
        }
    }


    BaseScreen(
        title = "Notificaciones",
        onIrHome = onIrHome,
        onIrPerfil = onIrPerfil,
        onGestionServicios = onIrServicios,
        onLogOut = onLogOut,
        onNotificationsClick = onIrNotificaciones,
        onVolver = onVolver,
        notificationBadgeCount = notificaciones.count { !it.leida }
    ) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(GrisFondoPantalla)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con título y botón de marcar como leídas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Notificaciones",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Naranja
                    )
                    Text(
                        text = "${notificaciones.count { !it.leida }} sin leer",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (notificaciones.isNotEmpty() && notificaciones.any { !it.leida }) {
                    TextButton(onClick = { marcarTodasComoLeidas = true }) {
                        Text("Marcar como leídas", color = Naranja, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Lista de notificaciones
            if (notificaciones.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Sin notificaciones",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes notificaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Se mostrarán aquí los cambios en tus averías",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(notificaciones.size) { index ->
                        val notificacion = notificaciones[index]
                        NotificationCard(
                            notificacion = notificacion,
                            onClick = {
                                // Marcar como leída al hacer click
                                if (!notificacion.leida) {
                                    repo.marcarNotificacionComoLeida(notificacion.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notificacion: Notificacion,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) Color.White else Color(0xFFFFF3E0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notificacion.leida) 1.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = notificacion.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Naranja,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notificacion.mensaje,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Badge de no leído
                if (!notificacion.leida) {
                    Badge(
                        modifier = Modifier.align(Alignment.Top),
                        containerColor = Naranja
                    ) {
                        Text("Nuevo", fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatearFecha(notificacion.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                if (!notificacion.leida) {
                    TextButton(onClick = onClick) {
                        Text("Marcar como leído", fontSize = 11.sp, color = Naranja)
                    }
                }
            }
        }
    }
}

/**
 * Convierte un timestamp a una fecha legible
 */
fun formatearFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Vista previa
 */
@Preview(showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen()
}

