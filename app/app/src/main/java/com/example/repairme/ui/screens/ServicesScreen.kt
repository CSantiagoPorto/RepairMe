package com.example.repairme.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.repairme.R
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onVolver: () -> Unit = {}
) {

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

            Image(
                painter = painterResource(id = R.drawable.clear_repair_principal),
                contentDescription = "Logo ClearRepair",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Text("Reparación clara y sencilla")

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Reparación de ordenadores")

                    HorizontalDivider()

                    Text("En ClearRepair ofrecemos diagnóstico y reparación de equipos informáticos.")

                    Text("Nuestro objetivo es que el cliente entienda en todo momento qué ocurre con su equipo.")

                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Seguimiento del estado")

                    HorizontalDivider()

                    Text("El cliente puede consultar el estado de su reparación desde la aplicación.")

                    Text("Los técnicos actualizan la información durante todo el proceso.")

                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Proceso de reparación")

                    HorizontalDivider()

                    Text("1. Recepción del equipo")

                    Text("2. Diagnóstico de la avería")

                    Text("3. Reparación del equipo")

                    Text("4. Entrega al cliente")

                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Información básica")

                    HorizontalDivider()

                    Text("Los datos del cliente se utilizan únicamente para la gestión de la reparación.")

                    Text("El cliente puede consultar el estado de su incidencia desde la aplicación.")

                    Text("Cualquier intervención se realiza con el objetivo de solucionar la avería detectada.")

                }
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