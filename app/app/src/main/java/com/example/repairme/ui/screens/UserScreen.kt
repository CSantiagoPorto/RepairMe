package com.example.repairme.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    verEquipos: () -> Unit = {},
    onAddEquipo:()->Unit={}
) {
    var equiposExpandido by remember { mutableStateOf(false) }
    var reparacionesExpandido by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = GrisFondoPantalla,
        topBar = {
            TopAppBar(

                title = {
                    Text(
                        text = "ClearRepair",
                        color = naranjaLetras,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center

                    )
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(//Card de equipos
                modifier = Modifier.fillMaxWidth().clickable { equiposExpandido=!equiposExpandido },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis equipos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.Computer,
                        contentDescription = "Equipos",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }
                if (equiposExpandido){
                    HorizontalDivider()
                    Row (
                        modifier= Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        TextButton(onClick = {verEquipos()}) {
                            Text("Ver equipos", color = Naranja)
                        }
                    }
                }
            }


            Card(//Card de reparaciones
                modifier = Modifier.fillMaxWidth().clickable { reparacionesExpandido=!reparacionesExpandido},
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis reparaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = "Reparaciones",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }//Aquí termina el Scaffold


}

@Preview(showSystemUi = true)
@Composable
fun UserScreenPreview() {
    UserScreen()
}