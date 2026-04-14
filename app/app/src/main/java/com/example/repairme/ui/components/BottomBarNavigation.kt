package com.example.repairme.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.R
import com.example.repairme.ui.theme.Naranja
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp

// Esta clase es un "contenedor" para guardar la información de cada botón de la barra inferior.
data class NavItem(
    val label: String,  // Texto que aparece debajo del boton, si no queremos texto se borra
    val icon: ImageVector,  // Icono que aparece en el boton
    val onClick: () -> Unit  // Función que se ejecuta al hacer click en el boton
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    title: String = "ClearRepair", // Titulo por defecto
    onIrHome: () -> Unit = {}, // Accion boton home: Navega a la pantalla principal del rol
    onIrPerfil: () -> Unit, // Accion boton perfil: Navega a la pantalla de perfil del usuario
    onGestionServicios: () -> Unit, // Accion boton gestion de servicios: Muestra info/opciones
    onLogOut: () -> Unit, // Accion boton salir: Cierra sesión y vuelve a login
    onVolver: (() -> Unit)? = null, // Botón volver opcional (en pantallas secundarias)
    onNotificationsClick: () -> Unit = {}, // Accion boton notificaciones: Navega a la pantalla de notificaciones
    notificationBadgeCount: Int = 0, // Número de notificaciones no leídas (contador global sincronizado)
    content: @Composable (Modifier) -> Unit // IMPORTANTE!! Aqui va el diseño de cada pantalla es el content que se va a mostrar
) {
    Scaffold(
        // Definimos la parte superior de la pantalla
        topBar = {
            TopAppBar(
                title = {
                    // Añadimos logo y texto juntos en la parte izquierda
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.clear_repair_mini), // Imagen del logotipo sacada de drawable
                            contentDescription = "Logo ClearRepair",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = title,
                            color = Naranja,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                },
                navigationIcon = {
                    onVolver?.let {
                        IconButton(onClick = it) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Naranja
                            )
                        }
                    }
                },
                // Botones de accion a la derecha , info, perfil y salir.
                actions = {
                    IconButton(onClick = onGestionServicios) {
                        Icon(Icons.Filled.Info, contentDescription = "Gestionar servicios", tint = Naranja)
                    }
                    IconButton(onClick = onLogOut) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = Naranja)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
       // BARRA NAVEGACION INFERIOR
        // Contiene los botones: Home, Notificaciones y Perfil
        // Esta barra es igual para todos los roles (User, Tecnico, Admin)
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavItem(
                    label = "Home",
                    icon = Icons.Filled.Home,
                    onClick = onIrHome
                ).let { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 10.sp) },
                        selected = false,
                        onClick = item.onClick,
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Naranja,
                            unselectedTextColor = Naranja
                        )
                    )
                }

                // Navega a la pantalla de notificaciones que es común para todos los roles
                // Muestra un badge con el número de notificaciones no leídas (contador global)
                // Si hay notificaciones, muestra: "1", "5", "99+" si hay más de 99
                NavigationBarItem(
                    icon = {
                        // BadgedBox: Contenedor que permite mostrar un badge con número
                        BadgedBox(
                            badge = {
                                // Si hay notificaciones no leídas, muestra el badge con el contador
                                if (notificationBadgeCount > 0) {
                                    Badge {
                                        Text(
                                            text = if (notificationBadgeCount > 99) "99+" else notificationBadgeCount.toString(),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            // Icono de notificaciones
                            Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
                        }
                    },
                    label = { Text("Notificaciones", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNotificationsClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Naranja,
                        unselectedTextColor = Naranja
                    )
                )

                NavItem(
                    label = "Perfil",
                    icon = Icons.Filled.PermIdentity,
                    onClick = onIrPerfil
                ).let { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 10.sp) },
                        selected = false,
                        onClick = item.onClick,
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Naranja,
                            unselectedTextColor = Naranja
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Le pasamos innerPadding para que el contenido no se tape con las barras
        // Esto lo que hace es que entre las 2 barras pongamos el contenido que queramos
        content(Modifier.padding(innerPadding))
    }
}
