package com.example.repairme.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import com.example.repairme.R
import com.example.repairme.data.model.Servicio
import com.example.repairme.data.repository.ServiceRepository
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onVolver: () -> Unit = {}
) {

    val repo = remember { ServiceRepository() }
    var listaServicios by remember { mutableStateOf(listOf<Servicio>()) }

    // 🔹 Cargar servicios de Firebase
    LaunchedEffect(Unit) {
        repo.obtenerServicios(
            fallo = {},
            exito = { listaServicios = it }
        )
    }

    Scaffold(
        containerColor = GrisFondoPantalla,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuestros servicios", color = naranjaLetras) },
                navigationIcon = {
                    TextButton(onClick = { onVolver() }) {
                        Text("Volver", color = naranjaLetras)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = grisfondo
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔹 Logo arriba
            Image(
                painter = painterResource(id = R.drawable.clear_repair_principal),
                contentDescription = "Logo ClearRepair",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Text(
                text = "Cómo trabajamos en ClearRepair",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Nuestro objetivo es ofrecer una reparación clara y transparente para el cliente."
            )

            // 🔹 LISTA DINÁMICA DESDE FIREBASE
            listaServicios.forEach { servicio ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = servicio.titulo,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = servicio.descripcion)
                    }
                }
            }

            // 🔹 Si no hay servicios
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
    ServicesScreen()
}