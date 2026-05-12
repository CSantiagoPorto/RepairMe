
package com.example.repairme.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import com.example.repairme.data.model.Servicio
import com.example.repairme.data.repository.ServiceRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.naranjaLetras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onIrHome: () -> Unit,
    onIrPerfil: () -> Unit,
    onGestionServicios: () -> Unit,
    onLogOut: () -> Unit,
    onVolver: () -> Unit,
    onNotificationsClick: () -> Unit,
    notificationBadgeCount: Int
) {

    val repo = remember { ServiceRepository() }
    var listaServicios by remember { mutableStateOf(listOf<Servicio>()) }

    //  Cargar servicios de Firebase
    LaunchedEffect(Unit) {
        repo.obtenerServicios(
            fallo = {},
            exito = { listaServicios = it }
        )
    }

    BaseScreen(
        title = "Nuestros servicios",
        onIrHome = onIrHome,
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,
        onNotificationsClick = onNotificationsClick,
        notificationBadgeCount = notificationBadgeCount
    ) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(GrisFondoPantalla)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Cómo trabajamos en ClearRepair",
                fontWeight = FontWeight.Bold,
                color = naranjaLetras
            )

            Text(
                text = "Nuestro objetivo es ofrecer una reparación clara y transparente para el cliente."
            )

            // Creamos la lista de servicios desde Firebase
            listaServicios.forEach { servicio ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = servicio.titulo,
                            fontWeight = FontWeight.Bold,
                            color = naranjaLetras
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = servicio.descripcion)
                    }
                }
            }

            // Si no hay servicios entra por aquí
            if (listaServicios.isEmpty()) {
                Text("No hay servicios disponibles")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {
    ServicesScreen(
        onIrHome = {},
        onIrPerfil = {},
        onGestionServicios = {},
        onLogOut = {},
        onVolver = {},
        onNotificationsClick = {},
        notificationBadgeCount = 0
    )
}