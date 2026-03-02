package com.example.repairme.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Equipo
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.ui.screens.auth.BottomNavButton
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onAddEquipo:()->Unit={},
    onVolver:()->Unit={},
    onVerEquipos:(Equipo)-> Unit={}

) {
    var equiposExpandido by remember { mutableStateOf(false) }
    var reparacionesExpandido by remember { mutableStateOf(false) }
    var listaEquipos by remember { mutableStateOf(listOf<Equipo>()) }
    LaunchedEffect(equiposExpandido) {
        val repo= DeviceRepository()
        if(equiposExpandido){//Esot sólo se va a cargar cuando la card se abra
            repo.obtenerEquipos(
                error = {
                    mensaje-> Log.d("Si ves este mensaje es porque no está obteniendo los equipos", mensaje)
                    onVolver()},
                exito = {equipos ->
                    Log.d("Log de UserScreen", "Equipos recibido: ${equipos.size}")
                    listaEquipos= equipos }
            )
        }
    }

    Scaffold(
        containerColor = GrisFondoPantalla,
        topBar = {
            CenterAlignedTopAppBar(

                title = {
                    Text(
                        text = "ClearRepair",
                        color = naranjaLetras,
                        fontWeight = FontWeight.Bold,
                       // textAlign = TextAlign.Center

                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = grisfondo
                )
            )
        },
        bottomBar = {//RECUERDA PREGUNTARLES A LOS CHICOS SI ESTO LO HACEMOS COMPARTIBLE
            Row (
                modifier= Modifier. fillMaxWidth().background(Color.White).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                BottomNavButton(icon= Icons.Filled.Computer, label = "Mis equipos", color= Naranja, onClick = {})
                BottomNavButton(icon= Icons.Filled.Build, label = "Mis reparaciones", color= Naranja, onClick = {})
                BottomNavButton(icon= Icons.Filled.Notifications, label = "Mis notificaciones", color= Naranja, onClick = {})
                BottomNavButton(icon= Icons.Filled.Person, label = "Mi Perfil", color= Naranja, onClick = {})
            }

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
                        TextButton(onClick = {onAddEquipo()}) {
                            Text("Añadir equipo", color = Naranja)
                        }
                    }
                    Column (//Espaciar las tarjetas
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)

                    ){
                        listaEquipos.forEach{equipo-> Card (
                            modifier = Modifier.fillMaxWidth().padding(18.dp).clickable { //Hay que crear la función de ver equipo
                                onVerEquipos(equipo) },
                            shape= RoundedCornerShape(9.dp),
                            border = BorderStroke(2.dp, Naranja)
                        ){
                            Row (
                                modifier= Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Text(text = "${equipo.deviceBrand} ${equipo.deviceModel}")
                            }
                        }
                            /*Text(
                            //Ahora mismo los equipos se muestran feos, en un text
                            //Esto mejor convertirlo en otra card
                            text = "${equipo.deviceBrand} ${equipo.deviceModel}"
                        ) */
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
    }

}

@Preview(showSystemUi = true)
@Composable
fun UserScreenPreview() {
    UserScreen()
}